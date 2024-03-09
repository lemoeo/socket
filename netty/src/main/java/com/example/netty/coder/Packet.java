package com.example.netty.coder;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;

import java.io.Serializable;

/**
 * TCP通信消息协议
 * 协议头 + 协议体：消息长度（4个字节） + 消息内容
 */
public class Packet implements Serializable, Cloneable {
    private int bodyLength;
    private byte[] body;
    private JSONObject jsonObject;

    public Packet(int bodyLength, byte[] body, JSONObject jsonObject) {
        this.bodyLength = bodyLength;
        this.body = body;
        this.jsonObject = jsonObject;
    }

    public Packet(byte[] body) {
        this.bodyLength = body.length;
        this.body = body;
        this.jsonObject = JSON.parseObject(body);
    }

    public Packet(JSONObject jsonObject) {
        final byte[] b = JSON.toJSONBytes(jsonObject);
        this.bodyLength = b.length;
        this.body = b;

        /*String bodyStr = jsonObject.toString();
        byte[] bodyBytes = bodyStr.getBytes();
        this.bodyLength = bodyBytes.length;
        this.body = bodyBytes;*/
    }

    public int getBodyLength() {
        return bodyLength;
    }

    public void setBodyLength(int bodyLength) {
        this.bodyLength = bodyLength;
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public JSONObject getJsonObject() {
        return jsonObject;
    }

    public void setJsonObject(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    @Override
    public Packet clone() {
        try {
            Packet clone = (Packet) super.clone();
            byte[] body = clone.getBody();
            clone.setBody(body.clone());
            clone.setJsonObject(clone.jsonObject.clone());
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}