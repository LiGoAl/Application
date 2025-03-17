package com.example.springbootguide.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
@Setter
public class DepartmentDTO {
    private Long departmentId;
    private String departmentName;
    private Integer employeeSize;
    private BigDecimal averageSalary;
}
