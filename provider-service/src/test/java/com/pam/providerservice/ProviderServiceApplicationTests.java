package com.pam.providerservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;


@SpringJUnitConfig
class ProviderServiceApplicationTests {


    @Test
    void contextLoads() {
        try (var mockedSpringApplication = mockStatic(SpringApplication.class)) {
            // when
            ProviderServiceApplication.main(new String[]{});

            // then
            mockedSpringApplication.verify(
                () -> SpringApplication.run(
                    eq(ProviderServiceApplication.class),
                    any(String[].class)
                )
            );
        }
    }

    @Test
    void constructorCreatesInstance() {
        // when
        ProviderServiceApplication application = new ProviderServiceApplication();

        // then
        assertThat(application).isNotNull();
    }
}
