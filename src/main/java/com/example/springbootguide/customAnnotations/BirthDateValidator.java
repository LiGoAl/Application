package com.example.springbootguide.customAnnotations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.Period;

public class BirthDateValidator implements ConstraintValidator<ValidBirthDate, LocalDate> {

    @Override
    public void initialize(ValidBirthDate constraintAnnotation) {
    }

    @Override
    public boolean isValid(LocalDate localDate, ConstraintValidatorContext context) {
        if (localDate == null) {
            return false;
        }
        LocalDate now = LocalDate.now();
        int age = Period.between(localDate, now).getYears();
        return age >= 18;
    }
}
