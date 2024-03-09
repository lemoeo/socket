package com.example.netty.handler;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.example.netty.ChannelManager;
import com.example.netty.coder.Packet;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.CharsetUtil;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
@ChannelHandler.Sharable
public class SimplePacketHandler extends SimpleChannelInboundHandler<Packet> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Packet msg) throws Exception {
        Channel channel = ctx.channel();
        String body = new String(msg.getBody(), CharsetUtil.UTF_8);
        System.out.printf("收到客户端[%s]发送的消息：%s%n", channel.remoteAddress(), body);
        JSONObject obj = JSON.parseObject(body);
        if (obj != null) {
            int type = obj.getIntValue("type");
            switch (type) {
                case 0: // 心跳包
                    channel.writeAndFlush(msg); // 发送心跳回复
                    break;
                case 1: // 设备绑定
                    String deviceSn = obj.getString("deviceSn");
                    // 将Channel与设备sn绑定
                    ChannelManager.online(channel, deviceSn);
                    channel.writeAndFlush(msg);
                    break;
                case 2: // 客户端已收到请假指令回复的消息
                    int vacationGuardLogId = obj.getIntValue("vacationGuardLogId");
                    // do something...
                    System.out.println("请假指令已执行完毕！");
                    break;
                case 3: // 客户端执行远程控制指令完成回复的消息
                    // do something...
                    System.out.println("远程控制指令执行完毕！");
                    break;
                default:
                    System.out.println("未知消息！");
                    break;
            }
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        System.out.printf("接收客户端[%s]消息完毕！%n", ctx.channel().remoteAddress());

        /*Packet sendPacket = new Packet(5, "Hello".getBytes(StandardCharsets.UTF_8));
        System.out.println("发送消息给客户端："+ctx.channel().remoteAddress());
        ctx.channel().writeAndFlush(sendPacket);*/
        super.channelReadComplete(ctx);
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        System.out.printf(Locale.US, "客户端[%s]已注册%n", ctx.channel().remoteAddress());
        super.channelRegistered(ctx);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.printf(Locale.US, "客户端[%s]已连接%n", ctx.channel().remoteAddress());
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        final Channel channel = ctx.channel();
        System.out.printf(Locale.US, "客户端[%s]已断开%n", channel.remoteAddress());
        ChannelManager.offline(channel);
        super.channelInactive(ctx);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        System.out.printf(Locale.US, "客户端[%s]已注销%n", ctx.channel().remoteAddress());
        super.channelUnregistered(ctx);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        System.out.printf("客户端[%s] 用户事件%n", ctx.channel().remoteAddress());
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.READER_IDLE) {
                // 在规定时间内没有收到客户端的上行数据, 主动断开连接
                //log.info("{}超时，断开连接", ctx.channel().id());
                ctx.close();
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.printf("客户端[%s] 发生异常！%n", ctx.channel().remoteAddress());
        //super.exceptionCaught(ctx, cause);
        ctx.close();
    }
}
