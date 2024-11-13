package com.pam.patientservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.springframework.test.util.AssertionErrors.assertEquals;

@SpringBootTest
class PatientServiceApplicationTests {

	@Test
	void testArrayOperations() {
		int[] numbers = {1, 2, 3, 4, 5};
		assertEquals("Array length should be 5", 5, numbers.length);
		assertEquals("First element should be 1", 1, numbers[0]);
		assertEquals("Last element should be 5", 5, numbers[4]);
	}
}
