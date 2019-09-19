package com.xjwx.fognode;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication
public class FogNodeApplication {

    public static void main(String[] args) {
        SpringApplication.run(FogNodeApplication.class, args);
    }

}
