package com.example.demo;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

@Component
@Qualifier("nettyDemoTcpByteToCharDecoder")
public class NettyDemoTcpByteToCharDecoder extends ByteToMessageDecoder{

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		//tcp에 따라서 앞 4바이트는 약속 되어 있기 때문에 4바이트 이하면 받지 않는다.
		if (in.readableBytes() < 4) {
			return;
		}
		
		int msgLength = in.readInt();
		
		//읽을 수 있는 전체 바이트의 값이 msg길이 보다 작으면 문자가 깨짐으로 readerIndex 의 위치를 리셋한다
		if(in.readableBytes() < msgLength) {
			in.resetReaderIndex();
			return;
		}
		
		out.add(in);
	}
}
