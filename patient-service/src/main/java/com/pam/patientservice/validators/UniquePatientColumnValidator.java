package com.pam.patientservice.validators;


import com.pam.patientservice.constraints.UniquePatientColumn;
import com.pam.patientservice.repository.PatientRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
public class UniquePatientColumnValidator implements ConstraintValidator<UniquePatientColumn, String> {

    @Autowired
    private PatientRepository patientRepository;

    @Override
    public boolean isValid(String mrn, ConstraintValidatorContext context) {
        if (patientRepository == null) {
            return true;
        }

        if (mrn == null) {
            return true;
        }
        return !patientRepository.existsByMrn(mrn);
    }
}
