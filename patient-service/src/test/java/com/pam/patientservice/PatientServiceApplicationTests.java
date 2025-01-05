package com.pam.patientservice;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Duration;

@SpringBootTest(properties = {
    "PATIENT_SERVICE_HOSTNAME=localhost",
    "PATIENT_SERVICE_DB=testdb",
    "PATIENT_SERVICE_USERNAME=user",
    "PATIENT_SERVICE_PASSWORD=password",
    "SHOULD_SHOW_SQL=true"
})
@Testcontainers
@ActiveProfiles("dev")
class PatientServiceApplicationTests {
    @Container
    public static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
        .withDatabaseName("testdb")
        .withUsername("user")
        .withPassword("password")
        .waitingFor(Wait.forListeningPort())
        .withStartupTimeout(Duration.ofSeconds(30));

    static {
        postgres.start();
    }

    @DynamicPropertySource
    static void registerPgProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.flyway.url", postgres::getJdbcUrl);
        registry.add("spring.flyway.user", postgres::getUsername);
        registry.add("spring.flyway.password", postgres::getPassword);
    }

    @Test
    @DisplayName("Verify Spring context loads successfully")
    void contextLoads() {
        // Context load test - will fail if application context cannot start
    }
}
