package com.example.springbootguide.model;

import com.example.springbootguide.customAnnotation.ValidBirthDate;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;

@Entity
@Table(name = "employee")
@NoArgsConstructor
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
    @Getter
    @Positive(message = "Id must be greater than 0")
    private Long id;
    @Getter
    @NotBlank(message = "Name can't be empty")
    private String name;
    @Getter
    @Setter
    @Pattern(regexp = "\\w+@\\w+\\.\\w+",message = "Email doesn't match the form")
    private String email;
    @Getter
    @NotNull
    @ValidBirthDate
    private LocalDate birthDate;
    @Getter
    @Setter
    @DecimalMin(value = "5000", message = "Salary must be bigger than 5000")
    @NotNull
    private BigDecimal salary;
    @Transient
    private Integer age;
    @JsonBackReference
    @ManyToOne
    @Getter
    @Setter
    @JoinColumn(name = "department_id")
    private Department department;

    public Employee(Long id, String name, String email, LocalDate birthDate, BigDecimal salary) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.birthDate = birthDate;
        this.salary = salary;
        this.age = Period.between(birthDate, LocalDate.now()).getYears();
    }

    public Integer getAge() {
        if (this.age == null) {
            this.age = Period.between(birthDate, LocalDate.now()).getYears();
        }
        return age;
    }

    @JsonProperty("departmentId")
    public Long getDepartmentId() {
        return department.getId();
    }
}
