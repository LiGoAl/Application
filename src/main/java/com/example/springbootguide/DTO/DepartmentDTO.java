package com.example.springbootguide.DTO;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
public class DepartmentDTO {
    @Getter
    private Long departmentId;
    @Getter
    private String departmentName;
    @Getter
    private Integer employeeSize;
    @JsonIgnore
    private Double averageDoubleSalary;
    private BigDecimal averageSalary;

    public DepartmentDTO(Long departmentId, String departmentName, Integer employeeSize, Double averageDoubleSalary) {
        this.departmentId = departmentId;
        this.departmentName = departmentName;
        this.employeeSize = employeeSize;
        this.averageDoubleSalary = averageDoubleSalary;
    }

    public BigDecimal getAverageSalary() {
        if (averageSalary == null) {
            if (averageDoubleSalary != null) {
                return BigDecimal.valueOf(averageDoubleSalary);
            }
        }
        return averageSalary;
    }
}
