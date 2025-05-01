package com.example.springbootguide.DTO;

import com.example.springbootguide.customAnnotation.ValidBirthDate;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;

@Getter
@Setter
public class EmployeeDTO {
    private Long id;
    @NotBlank(message = "Name can't be empty")
    private String name;
    @Pattern(regexp = "\\w+@\\w+\\.\\w+",message = "Email doesn't match the form")
    @NotNull(message = "Email doesn't match the form")
    private String email;
    @ValidBirthDate
    private LocalDate birthDate;
    @DecimalMin(value = "5000", message = "Salary must be bigger than 5000")
    @NotNull(message = "Salary must be bigger than 5000")
    private BigDecimal salary;
    private Integer age;
    @Positive(message = "Id must be greater than 0")
    private Long departmentId;

    public EmployeeDTO(Long id, String name, String email, LocalDate birthDate, BigDecimal salary, Long departmentId) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.birthDate = birthDate;
        this.salary = salary;
        this.departmentId = departmentId;
        if (birthDate != null) {
            this.age = Period.between(birthDate, LocalDate.now()).getYears();
        }
    }
}
