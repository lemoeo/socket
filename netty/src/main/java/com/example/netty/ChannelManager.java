package com.example.netty;

import io.netty.channel.Channel;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

import java.util.concurrent.ConcurrentHashMap;

public class ChannelManager {
    private static final ConcurrentHashMap<String, Channel> channelMap = new ConcurrentHashMap<>();

    /**
     * 判断一个通道是否有用户在使用
     * 可做信息转发时判断该通道是否合法
     *
     * @param channel
     * @return
     */
    public static boolean hasUser(Channel channel) {
        AttributeKey<String> key = AttributeKey.valueOf("user");
        //netty移除了这个map的remove方法,这里的判断谨慎一点
        return (channel.hasAttr(key) || channel.attr(key).get() != null);
    }

    /**
     * 上线一个用户
     *
     * @param channel
     * @param userId
     */
    public static void online(Channel channel, String userId) {
        //先判断用户是否在web系统中登录?
        //这部分代码个人实现
        channelMap.put(userId, channel);
        AttributeKey<String> key = AttributeKey.valueOf("user");
        channel.attr(key).set(userId);

        System.out.println("在线个数："+channelMap.size());
    }

    /**
     * 根据用户id获取该用户的通道
     *
     * @param userId
     * @return
     */
    public static Channel getChannelByUserId(String userId) {
        return channelMap.get(userId);
    }

    /**
     * 判断一个用户是否在线
     *
     * @param userId
     * @return
     */
    public static Boolean online(String userId) {
        return channelMap.containsKey(userId) && channelMap.get(userId) != null;
    }

    /**
     * 下线一个用户
     *
     * @param channel
     */
    public static void offline(Channel channel) {
        AttributeKey<String> key = AttributeKey.valueOf("user");
        Attribute<String> attribute = channel.attr(key);
        if (attribute != null && attribute.get() != null) {
            if (!channelMap.get(attribute.get()).isActive()) {
                System.out.println("remove channel");
                channelMap.remove(attribute.get());
                attribute.set(null);
            }
        }
    }
}
