package com.example.sparrow.discovery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class SparrowDiscoveryServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(SparrowDiscoveryServiceApplication.class, args);
    }
}
