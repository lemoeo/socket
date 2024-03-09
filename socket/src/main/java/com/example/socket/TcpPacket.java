package com.example.socket;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

public class TcpPacket {
    private final int HEAD_SIZE = 4; // 定义消息头占4个字节

    /**
     * 包装消息数据，在消息头部添加长度
     */
    public byte[] pack(String content) {
        byte[] body = content.getBytes(StandardCharsets.UTF_8);
        ByteBuffer buffer = ByteBuffer.allocate(HEAD_SIZE + body.length);
        buffer.putInt(body.length);
        buffer.put(body);
        return buffer.array();
    }

    /**
     * 解包消息数据
     */
    public String unpack(byte[] bytes) {
        return "";
    }

    /**
     * 获取消息头的内容(也就是消息体的长度)
     * @param inputStream
     * @return
     */
    public int getHeader(InputStream inputStream) throws Exception {
        int result = 0;
        byte[] bytes = new byte[HEAD_SIZE];
        int value = inputStream.read(bytes, 0, HEAD_SIZE);
        if (value == -1) {
            throw new IOException("Read data failed, maybe the Socket is disconnected.");
        }
        ByteBuffer bb = ByteBuffer.wrap(bytes);
        bb.order(ByteOrder.BIG_ENDIAN);
        // 得到消息体的字节长度
        result = bb.getInt();
        return result;
    }

}
