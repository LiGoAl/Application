package com.example.springbootguide.controllers;

import com.example.springbootguide.DTO.EmployeeDTO;
import com.example.springbootguide.exceptions.RequestValidationException;
import com.example.springbootguide.services.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/employees")
@Validated
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping
    public List<EmployeeDTO> readEmployees(@RequestParam(defaultValue = "0") Integer page,
                                           @RequestParam(defaultValue = "5") Integer size) {
        return employeeService.getEmployees(page, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EmployeeDTO createEmployee(@Valid @RequestBody EmployeeDTO employeeDTO) {
        return employeeService.createEmployee(employeeDTO);
    }

    @DeleteMapping("/{employeeId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEmployee(@PathVariable("employeeId") Long id) {
        employeeService.deleteEmployee(validatedEmployeeId(id));
    }

    @PutMapping("/{employeeId}")
    public void updateEmployee(@PathVariable("employeeId") Long id,
                               @RequestParam(value = "email", required = false) String email,
                               @RequestParam(value = "salary", required = false) BigDecimal salary,
                               @RequestParam(value = "departmentId", required = false) Long departmentId) {
        employeeService.updateEmployee(validatedEmployeeId(id),
                validatedEmployeeEmail(email),
                validatedEmployeeSalary(salary),
                validatedEmployeeDepartmentId(departmentId));
    }

    private Long validatedEmployeeId(Long id) {
        if (id == null || id <= 0) {
            throw new RequestValidationException("Id must be greater than 0 and can't be empty");
        } else return id;
    }

    private String validatedEmployeeEmail(String email) {
        if (email != null && !email.matches("\\w+@\\w+\\.\\w+")) {
            throw new RequestValidationException("Email doesn't match the form");
        } else return email;
    }

    private BigDecimal validatedEmployeeSalary(BigDecimal salary) {
        if (salary != null && salary.compareTo(BigDecimal.valueOf(5000)) <= 0) {
            throw new RequestValidationException("Salary must be bigger than 5000");
        } else return salary;
    }

    private Long validatedEmployeeDepartmentId(Long departmentId) {
        if (departmentId != null && departmentId <= 0) {
            throw new RequestValidationException("Department id must be greater than 0");
        } else return departmentId;
    }
}
