package com.example.springbootguide.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
@Setter
public class DepartmentDTO {
    private Long departmentId;
    @NotBlank(message = "Name can't be empty")
    private String departmentName;
    private Integer employeeSize;
    private BigDecimal averageSalary;
}
