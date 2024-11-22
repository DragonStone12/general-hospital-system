package com.pam.patientservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.springframework.test.util.AssertionErrors.assertEquals;

@SpringBootTest
class PatientServiceApplicationTests {

	@Test
	void main_shouldStartApplication() {
		PatientServiceApplication.main(new String[]{});
	}
}
