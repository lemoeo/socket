package com.example.netty;

import com.example.netty.coder.PacketDecoder;
import com.example.netty.coder.PacketEncoder;
import com.example.netty.handler.PacketHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.springframework.stereotype.Component;

@Component
public class ChannelHandlerInitializer extends ChannelInitializer<SocketChannel> {

    private final PacketHandler handler;

    public ChannelHandlerInitializer(PacketHandler handler) {
        this.handler = handler;
    }

    //private final SimplePacketHandler handler;

    /*public ChannelHandlerInitializer(SimplePacketHandler handler) {
        this.handler = handler;
    }*/

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();
        //添加对于读写大数据流的支持
        pipeline.addLast(new ChunkedWriteHandler());
        //对httpMessage进行聚合
        pipeline.addLast(new HttpObjectAggregator(1024 * 64));
        pipeline.addLast("decoder", new PacketDecoder());
        pipeline.addLast("encoder", new PacketEncoder());
        //自定义handler
        pipeline.addLast(handler);
    }
}
