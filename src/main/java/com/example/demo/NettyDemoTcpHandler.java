package com.example.demo;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import com.example.service.NettyDemoTcpChannelRepository;
import com.example.service.SpringDemoMapper;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

@Component
@Qualifier("nettyDemoTcpHandler")
@PropertySource(value= "classpath:/nettyserver.properties")
@ChannelHandler.Sharable
public class NettyDemoTcpHandler extends ChannelInboundHandlerAdapter{
	
	@Autowired
	private SpringDemoMapper springDemoMapper;
	
	@Autowired
	private NettyDemoTcpChannelRepository channelRepository;
	
	@Value("${tcp.separator}")
	private String separator;
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		ctx.fireChannelActive();
		String channelKey = ctx.channel().remoteAddress().toString();
		channelRepository.put(channelKey, ctx.channel());
	}
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		//TODO 비지니스 로직 
		ByteBuf byteBuf = (ByteBuf) msg;
		
		String tmpStr = (String) byteBufToHandleArray(byteBuf);
		springDemoMapper.addEntity(objSeparator(tmpStr));
		
		ReferenceCountUtil.release(msg);
	}
	
	public Object byteBufToHandleArray(ByteBuf in) {
		int length = in.readableBytes();
		
		if(in.hasArray()) {
			byte[] array = in.array();
			int offset = in.arrayOffset();
			
			return handleArray(array, offset, length);
		}else {
			byte[] array = new byte[length];
			in.getBytes(in.readerIndex(),array);
			
			return handleArray(array, 0, length);
		}
	}
	
	private String handleArray(byte[] array, int offset, int length) {
		String result = new String(array, offset, length);
		return result;
	}

	private Map<String, Object> objSeparator(Object msg) {
		//TODO 엔티티 객체화 
		Map<String, Object> entity = new HashMap<String, Object>();
		String msgStr = (String) msg;
		String[] strArr =  msgStr.split(separator);
		
		if(strArr.length > 1) {
			entity.put("paramStr1", strArr[0]);
			entity.put("paramStr2", strArr[1]);
		}
		
		return entity;
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.writeAndFlush(Unpooled.EMPTY_BUFFER)
			.addListener(ChannelFutureListener.CLOSE);
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.close();
	}
	
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		String channelKey = ctx.channel().remoteAddress().toString();
        this.channelRepository.remove(channelKey);
	}

    public void setChannelRepository(NettyDemoTcpChannelRepository channelRepository) {
        this.channelRepository = channelRepository;
    }
}
