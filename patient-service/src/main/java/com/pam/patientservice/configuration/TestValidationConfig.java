package com.pam.patientservice.configuration;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorFactory;
import jakarta.validation.Validation;
import jakarta.validation.ValidationException;
import jakarta.validation.ValidatorFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestValidationConfig {

    @Bean
    public ValidatorFactory validatorFactory() {
        return Validation.byDefaultProvider()
            .configure()
            .constraintValidatorFactory(new TestConstraintValidatorFactory())
            .buildValidatorFactory();
    }


    public static class TestConstraintValidatorFactory implements ConstraintValidatorFactory {

        @Autowired
        private ApplicationContext context;

        @Override
        public <T extends ConstraintValidator<?, ?>> T getInstance(Class<T> key) {
            try {

                return context.getBean(key);
            } catch (NoSuchBeanDefinitionException e) {

                try {
                    return key.getDeclaredConstructor().newInstance();
                } catch (Exception ex) {
                    throw new ValidationException("Could not instantiate validator: " + key.getName(), ex);
                }
            }
        }

        @Override
        public void releaseInstance(ConstraintValidator<?, ?> instance) {

        }
    }
}
