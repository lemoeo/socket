package com.example.socket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class LengthFieldTcpClient {

    public void connect() {
        try (Socket socket = new Socket("localhost", 8081);
             DataInputStream inputStream = new DataInputStream(socket.getInputStream());
             DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream())
        ) {
            System.out.println("Connected to server.");
            heartBeat(inputStream, outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            System.out.println("Disconnected from server.");
        }
    }

    public void heartBeat(DataInputStream inputStream, DataOutputStream outputStream) throws IOException {
        for (;;) {
            String message = "{\"type\":0}";
            byte[] messageBytes = message.getBytes(StandardCharsets.UTF_8);
            outputStream.writeInt(messageBytes.length); // 写入消息长度
            outputStream.write(messageBytes); // 写入消息内容
            outputStream.flush();
            int responseLength = inputStream.readInt(); // 读取响应消息长度
            byte[] responseBytes = new byte[responseLength];
            inputStream.readFully(responseBytes); // 读取响应消息内容
            String response = new String(responseBytes, StandardCharsets.UTF_8);
            System.out.println("Server response: " + response);
            if (response.equalsIgnoreCase("bye")) {
                break;
            }

            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {

            }
        }
    }

    public void handle(DataInputStream inputStream, DataOutputStream outputStream) throws IOException {
        Scanner scanner = new Scanner(System.in);
        for (;;) {
            System.out.print("Send: ");
            String message = scanner.nextLine();
            byte[] messageBytes = message.getBytes(StandardCharsets.UTF_8);
            outputStream.writeInt(messageBytes.length); // 写入消息长度
            outputStream.write(messageBytes); // 写入消息内容
            outputStream.flush();
            int responseLength = inputStream.readInt(); // 读取响应消息长度
            byte[] responseBytes = new byte[responseLength];
            inputStream.readFully(responseBytes); // 读取响应消息内容
            String response = new String(responseBytes, StandardCharsets.UTF_8);
            System.out.println("Server response: " + response);
            if (response.equalsIgnoreCase("bye")) {
                break;
            }
        }
    }
}
