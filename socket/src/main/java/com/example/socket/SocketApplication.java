package com.example.socket;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SocketApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(SocketApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        TcpServer.getInstance().start();
    }
}
