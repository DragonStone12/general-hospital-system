package com.pam.providerervice;

import com.pam.providerservice.ProviderServiceApplication;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;
import static org.assertj.core.api.Assertions.assertThat;

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



