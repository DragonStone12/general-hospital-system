package com.pam.providerservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.context.config.annotation.RefreshScope;

@SpringBootApplication
@RefreshScope
public class ProviderServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProviderServiceApplication.class, args);
    }
}
