package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

@Component
@Qualifier("nettyDemoTcpInitializer")
public class NettyDemoTcpInitializer extends ChannelInitializer<Channel>{

	@Autowired
    @Qualifier("nettyDemoTcpHandler")
    private ChannelInboundHandlerAdapter nettyDemoTcpHandler;
	
	@Autowired
    @Qualifier("nettyDemoTcpByteToCharDecoder")
	private ByteToMessageDecoder nettyDemoTcpByteToCharDecoder;
	
	
	@Override
	protected void initChannel(Channel ch) throws Exception {
		ChannelPipeline pipeline = ch.pipeline();
		pipeline.addFirst(new LoggingHandler(LogLevel.INFO)); //전송 데이터 확인용 핸들러 
		pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
		pipeline.addLast(new LoggingHandler(LogLevel.INFO));
		pipeline.addLast(nettyDemoTcpHandler);
	}
}
