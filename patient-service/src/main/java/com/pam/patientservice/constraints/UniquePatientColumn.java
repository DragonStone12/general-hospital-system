package com.pam.patientservice.constraints;

import com.pam.patientservice.validators.UniquePatientColumnValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UniquePatientColumnValidator.class)
public @interface UniquePatientColumn {
    String message() default "value must be unique";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
