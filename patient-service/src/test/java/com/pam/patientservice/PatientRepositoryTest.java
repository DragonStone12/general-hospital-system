package com.pam.patientservice;

import com.pam.patientservice.configuration.TestValidationConfig;
import com.pam.patientservice.entity.PatientDetails;
import com.pam.patientservice.repository.PatientRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@Testcontainers
@Tag("integration")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Transactional
@Import(TestValidationConfig.class)
class PatientRepositoryTest {

    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15.4")
        .withDatabaseName("testdb")
        .withUsername("test")
        .withPassword("test");

    @DynamicPropertySource
    static void registerPgProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.flyway.enabled", () -> "false");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private PatientRepository patientRepository;


    @Test
    void shouldSaveAndRetrievePatient() {

        PatientDetails patient = new PatientDetails();
        patient.setMrn("MRN12345");
        patient.setFirstName("John");
        patient.setLastName("Steinbeck");
        patient.setPhone("555-123-4567");
        patient.setIsActive(true);
        patient.setCreatedAt(LocalDateTime.now());
        patient.setUpdatedAt(LocalDateTime.now());

        PatientDetails savedPatient = patientRepository.save(patient);

        entityManager.flush();

        entityManager.clear();

        Optional<PatientDetails> foundPatient = patientRepository.findById(savedPatient.getId());

        // Assertions
        assertThat(foundPatient).isPresent();
        assertThat(foundPatient.get().getMrn()).isEqualTo("MRN12345");
        assertThat(foundPatient.get().getFirstName()).isEqualTo("John");
        assertThat(foundPatient.get().getLastName()).isEqualTo("Steinbeck");
    }
}
