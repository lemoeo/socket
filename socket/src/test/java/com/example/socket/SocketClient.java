package com.example.socket;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SocketClient {
    public static void main(String[] args) {
        ExecutorService executor = Executors.newCachedThreadPool();
        for (int i = 0; i< 3000; i++) {
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    new LengthFieldTcpClient().connect();
                }
            });
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {

            }
        }
    }
}
