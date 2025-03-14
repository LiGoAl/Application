package com.example.springbootguide.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class EmployeeDepartmentDTO {
    private Long employeeId;
    private String employeeName;
    private Long departmentId;
    private String departmentName;
}

