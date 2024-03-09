package com.example.socket;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.context.ApplicationContext;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TcpServer {
    private volatile static TcpServer instance;

    private TcpServer() {
    }

    public static TcpServer getInstance() {
        if (instance == null) {
            synchronized (TcpServer.class) {
                if (instance == null) {
                    instance = new TcpServer();
                }
            }
        }
        return instance;
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(8081);
             ExecutorService executor = Executors.newCachedThreadPool()) {
            System.out.println("Server is running...");
            for (;;) {
                Socket clientSocket = serverSocket.accept();
                executor.submit(() -> {
                    handler(clientSocket);
                });

                //executor.submit(() -> sendMsg(clientSocket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            System.out.println("Server is stopped!");
        }
    }

    private void handler(Socket clientSocket) {
        System.out.println("客户端连接："+clientSocket.getRemoteSocketAddress());
        try (   // 使用字节缓冲流
                BufferedInputStream inputStream = new BufferedInputStream(clientSocket.getInputStream());
                BufferedOutputStream outputStream = new BufferedOutputStream(clientSocket.getOutputStream())
        ) {
            // Socket 封装对象
            TcpPacket packet = new TcpPacket();
            while (true) {
                int length = packet.getHeader(inputStream); // 读取消息长度
                System.out.println(length);
                if (length == -1) {
                    break; // 客户端关闭连接
                }
                byte[] bodyBytes = new byte[length];
                int readCount;
                int bodyIndex = 0;
                System.out.println("bodyLength: "+length);
                while (bodyIndex <= (length - 1) &&
                        (readCount = inputStream.read(bodyBytes, bodyIndex, length)) != -1) { // 读取消息内容
                    bodyIndex += readCount;
                }
                readCount = 0;
                bodyIndex = 0;
                String bodyString = new String(bodyBytes, StandardCharsets.UTF_8);
                System.out.println("Client ["+ clientSocket.getRemoteSocketAddress()+"] Message: " + bodyString);
                String response;
                JSONObject responseObj = new JSONObject();
                try {
                    JSONObject jsonObject = new JSONObject(bodyString);
                    int type = jsonObject.optInt("type");
                    String data = jsonObject.optString("data");
                    switch (type) {
                        case 0: // 心跳
                            responseObj.put("type", 0);
                            break;
                        case 1: // 控制设备
                            responseObj.put("type", 1);
                            if (data.equals("1")) {
                                responseObj.put("data", "turn_on");
                            } else {
                                responseObj.put("data", "turn_off");
                            }
                            break;
                        default:
                            responseObj.put("type", -1);
                            break;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    try {
                        responseObj.put("response", bodyString);
                    } catch (JSONException ex) {
                        e.printStackTrace();
                    }
                }
                response = responseObj.toString();
                byte[] responseBytes = packet.pack(response);
                outputStream.write(responseBytes, 0, responseBytes.length);
                outputStream.flush();
                if (bodyString.equals("bye")) {
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("Client [" + clientSocket.getRemoteSocketAddress() + "] Disconnected.");
        }
    }

    public void sendMsg(Socket socket) {
        try (BufferedOutputStream outputStream = new BufferedOutputStream(socket.getOutputStream())) {
            TcpPacket packet = new TcpPacket();
            boolean enable = false;
            for (;;) {
                JSONObject object = new JSONObject();
                object.put("type", 1);
                try {
                    if (enable) {
                        object.put("data", "turn_on");
                    } else {
                        object.put("data", "turn_off");
                    }
                    System.out.println("发送" + (enable ? "开门" : "关门") + "指令");
                    outputStream.write(packet.pack(object.toString()));
                    outputStream.flush();
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }
                enable = !enable;
                Thread.sleep(5000);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
