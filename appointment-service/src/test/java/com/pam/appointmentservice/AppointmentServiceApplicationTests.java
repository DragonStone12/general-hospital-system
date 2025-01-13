package com.pam.appointmentservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;

@SpringJUnitConfig
class AppointmentServiceApplicationTests {

    @Test
    void contextLoads() {
        try (var mockedSpringApplication = mockStatic(SpringApplication.class)) {
            // when
            AppointmentServiceApplication.main(new String[]{});

            // then
            mockedSpringApplication.verify(
                    () -> SpringApplication.run(
                            eq(AppointmentServiceApplication.class),
                            any(String[].class)
                    )
            );
        }
    }

    @Test
    void constructorCreatesInstance() {
        // when
        AppointmentServiceApplication application = new AppointmentServiceApplication();

        // then
        assertThat(application).isNotNull();
    }
}
