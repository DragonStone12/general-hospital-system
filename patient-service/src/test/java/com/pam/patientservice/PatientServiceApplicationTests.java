package com.pam.patientservice;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
class PatientServiceApplicationTests {

    @Test
    @DisplayName("Verify main method doesn't throw any exceptions")
    void mainShouldStartApplication() {
        PatientServiceApplication.main(new String[]{});
    }
}
