package com.example.socket;

import org.springframework.stereotype.Repository;

@Repository
public interface TestMapper {
    public default void doSomething() {
        System.out.println("Do something");
    }
}
