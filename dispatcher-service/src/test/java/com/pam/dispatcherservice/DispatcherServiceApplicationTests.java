package com.pam.dispatcherservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;


@SpringJUnitConfig
class DispatcherServiceApplicationTests {

    @Test
    void contextLoads() {
        try (var mockedSpringApplication = mockStatic(SpringApplication.class)) {
            // when
            DispatcherServiceApplication.main(new String[]{});

            // then
            mockedSpringApplication.verify(
                () -> SpringApplication.run(
                    eq(DispatcherServiceApplication.class),
                    any(String[].class)
                )
            );
        }
    }

    @Test
    void constructorCreatesInstance() {
        // when
        DispatcherServiceApplication application = new DispatcherServiceApplication();

        // then
        assertThat(application).isNotNull();
    }
}

