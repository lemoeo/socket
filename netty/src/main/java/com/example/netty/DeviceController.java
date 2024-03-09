package com.example.netty;

import com.alibaba.fastjson2.JSONObject;
import com.example.netty.coder.Packet;
import io.netty.channel.Channel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DeviceController {
    @GetMapping("/device/{deviceSn}/{type}")
    public String device(@PathVariable("deviceSn") String deviceSn, @PathVariable("type") int type) {
        StringBuilder sb = new StringBuilder();
        sb.append("设备SN：").append(deviceSn).append(", ");
        Channel channel = ChannelManager.getChannelByUserId(deviceSn);
        JSONObject object = new JSONObject();
        object.put("type", type);
        int id = (int) (Math.random() * 1000);
        if (type == 2) { // 请假审批
            sb.append("vacationGuardLogId: ").append(id);
            object.put("vacationGuardLogId", id);
        } else if (type == 3) { // 远程开门
            sb.append("openId: ").append(id);
            object.put("openId", id);
        }
        channel.writeAndFlush(new Packet(object));
        return sb.toString();
    }

}
