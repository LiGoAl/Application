package com.example.springbootguide.controllers;

import com.example.springbootguide.DTO.DepartmentDTO;
import com.example.springbootguide.DTO.EmployeeDTO;
import com.example.springbootguide.DTO.EmployeeDepartmentDTO;
import com.example.springbootguide.services.DepartmentEmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/public")
@RequiredArgsConstructor
@Validated
public class DepartmentEmployeeController {

    private final DepartmentEmployeeService departmentEmployeeService;

    @GetMapping("/with-departments")
    public List<EmployeeDepartmentDTO> getAllEmployeesWithDepartments(@RequestParam(defaultValue = "0") Integer page,
                                                                      @RequestParam(defaultValue = "5") Integer size) {
        return departmentEmployeeService.getAllEmployeesWithDepartments(page, size);
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
    public List<EmployeeDTO> getEmployeesWithSalaryGreaterThan(@PathVariable("salary") BigDecimal salary,
                                                               @RequestParam(defaultValue = "0") Integer page,
                                                               @RequestParam(defaultValue = "5") Integer size) {
        return departmentEmployeeService.getEmployeesBySalaryGreaterThan(validatedSalary(salary), page, size);
    }

    @GetMapping("/top10-departments-by-average-salary")
    public List<DepartmentDTO> getTop10DepartmentsByAverageSalary() {
        return departmentEmployeeService.getTop10DepartmentsByAverageSalary();
    }

    private BigDecimal validatedSalary(BigDecimal salary) {
        if (salary.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Salary must be positive");
        } else return salary;
    }
}
