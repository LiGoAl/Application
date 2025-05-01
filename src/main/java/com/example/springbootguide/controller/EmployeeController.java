package com.example.springbootguide.controller;

import com.example.springbootguide.DTO.EmployeeDTO;
import com.example.springbootguide.exception.RequestValidationException;
import com.example.springbootguide.service.EmployeeService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
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
    public List<EmployeeDTO> readEmployees() {
        return employeeService.getEmployees();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EmployeeDTO createEmployee(@Valid @RequestBody EmployeeDTO employeeDTO) {
        return employeeService.createEmployee(employeeDTO);
    }

    @DeleteMapping("/{employeeId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEmployee(@PathVariable("employeeId") Long id) {
        if (id == null || id <= 0) {
            throw new RequestValidationException("Id must be greater than 0 and can't be empty");
        }
        employeeService.deleteEmployee(id);
    }

    @PutMapping("/{employeeId}")
    public void updateEmployee(@PathVariable("employeeId") Long id,
                               @RequestParam(value = "email", required = false) String email,
                               @RequestParam(value = "salary", required = false) BigDecimal salary,
                               @RequestParam(value = "departmentId", required = false) Long departmentId) {
        if (id == null || id <= 0) {
            throw new RequestValidationException("Id must be greater than 0 and can't be empty");
        }
        if (email != null && !email.matches("\\w+@\\w+\\.\\w+")) {
            throw new RequestValidationException("Email doesn't match the form");
        }
        if (salary != null && salary.compareTo(BigDecimal.valueOf(5000)) <= 0) {
            throw new RequestValidationException("Salary must be bigger than 5000");
        }
        if (departmentId != null && departmentId <= 0) {
            throw new RequestValidationException("Department id must be greater than 0");
        }
        employeeService.updateEmployee(id, email, salary, departmentId);
    }
}
