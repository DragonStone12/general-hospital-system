package com.pam.patientservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;
import static org.assertj.core.api.Assertions.assertThat;

@SpringJUnitConfig
class PatientServiceApplicationTests {

    @Test
    void contextLoads() {
        try (var mockedSpringApplication = mockStatic(SpringApplication.class)) {
            // when
            PatientServiceApplication.main(new String[]{});

            // then
            mockedSpringApplication.verify(
                () -> SpringApplication.run(
                    eq(PatientServiceApplication.class),
                    any(String[].class)
                )
            );
        }
    }

    @Test
    void constructorCreatesInstance() {
        // when
        PatientServiceApplication application = new PatientServiceApplication();

        // then
        assertThat(application).isNotNull();
    }
}



