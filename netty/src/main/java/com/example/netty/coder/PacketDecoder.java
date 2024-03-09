package com.example.netty.coder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * 消息解码器
 */
public class PacketDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        int readableLength = byteBuf.readableBytes(); // 获取可读字节数
        if (readableLength < 1) {
            return;
        }
        byteBuf.markReaderIndex();
        int bodyLength = byteBuf.readInt();
        if (readableLength < bodyLength + 4) {
            byteBuf.resetReaderIndex();
            return;
        }
        byte[] body = new byte[bodyLength];
        byteBuf.readBytes(body);
        list.add(new Packet(body));

        /*String json = new String(body);
        JSONObject jsonObject = JSON.parseObject(json);
        list.add(new Packet(bodyLength, body, jsonObject));*/
    }
}

