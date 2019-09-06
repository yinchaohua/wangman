package com.mg.sn;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class StarNodeApplication {

    public static void main(String[] args) {
        SpringApplication.run(StarNodeApplication.class, args);
    }

}
