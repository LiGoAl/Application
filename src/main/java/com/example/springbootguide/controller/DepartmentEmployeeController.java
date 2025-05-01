package com.example.springbootguide.controller;

import com.example.springbootguide.DTO.DepartmentDTO;
import com.example.springbootguide.DTO.EmployeeDTO;
import com.example.springbootguide.DTO.EmployeeDepartmentDTO;
import com.example.springbootguide.service.DepartmentEmployeeService;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Validated
public class DepartmentEmployeeController {

    private final DepartmentEmployeeService departmentEmployeeService;

    @GetMapping("/with-departments")
    public List<EmployeeDepartmentDTO> getAllEmployeesWithDepartments() {
        return departmentEmployeeService.getAllEmployeesWithDepartments();
    }

    @GetMapping("/most-employees")
    public DepartmentDTO getDepartmentWithMostEmployees() {
        return departmentEmployeeService.getDepartmentWithMostEmployees();
    }

    @GetMapping("/minimal-employees")
    public DepartmentDTO getDepartmentWithMinimalEmployees() {
        return departmentEmployeeService.getDepartmentWithMinimalEmployees();
    }

    @GetMapping("/salary-greater-than/{salary}")
    public List<EmployeeDTO> getEmployeesWithSalaryGreaterThan(@PathVariable("salary") @Positive(message = "Salary must be positive") BigDecimal salary) {
        return departmentEmployeeService.getEmployeesBySalaryGreaterThan(salary);
    }

    @GetMapping("/top10-departments-by-average-salary")
    public List<DepartmentDTO> getTop10DepartmentsByAverageSalary() {
        return departmentEmployeeService.getTop10DepartmentsByAverageSalary();
    }
}
