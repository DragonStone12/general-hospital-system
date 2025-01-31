package com.pam.dispatcherservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.context.config.annotation.RefreshScope;


@SpringBootApplication
@RefreshScope
public class DispatcherServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(DispatcherServiceApplication.class, args);
    }
}
