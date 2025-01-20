package com.pam.providerservice;

import org.springframework.boot.SpringApplication;

public class TestProviderServiceApplication {

    public static void main(String[] args) {
        SpringApplication.from(ProviderServiceApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
