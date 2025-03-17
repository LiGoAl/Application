package com.example.springbootguide.model;

import com.example.springbootguide.customAnnotation.ValidBirthDate;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "employee")
@NoArgsConstructor
@Getter
public class Employee {
    @Id
    @SequenceGenerator(
            name = "employee_sequence",
            sequenceName = "employee_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "employee_sequence"
    )
    @Positive(message = "Id must be greater than 0")
    private Long id;
    @NotBlank(message = "Name can't be empty")
    private String name;
    @Setter
    @Pattern(regexp = "\\w+@\\w+\\.\\w+",message = "Email doesn't match the form")
    private String email;
    @NotNull
    @ValidBirthDate
    private LocalDate birthDate;
    @Setter
    @DecimalMin(value = "5000", message = "Salary must be bigger than 5000")
    @NotNull
    private BigDecimal salary;
    @ManyToOne
    @Setter
    @JoinColumn(name = "department_id")
    private Department department;

    public Employee(Long id, String name, String email, LocalDate birthDate, BigDecimal salary) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.birthDate = birthDate;
        this.salary = salary;
    }
}
