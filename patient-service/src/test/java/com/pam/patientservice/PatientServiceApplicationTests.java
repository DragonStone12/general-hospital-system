package com.pam.patientservice;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;


@SpringBootTest
class PatientServiceApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
	@DisplayName("Verify main method doesn't throw any exceptions")
	void main_shouldStartApplication() {
		PatientServiceApplication.main(new String[]{});
	}
}