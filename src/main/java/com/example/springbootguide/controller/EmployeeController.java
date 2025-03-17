package com.example.springbootguide.controller;

import com.example.springbootguide.DTO.EmployeeDTO;
import com.example.springbootguide.service.EmployeeService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
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
    public EmployeeDTO createEmployee(@Valid @RequestBody EmployeeDTO employeeDTO) {
        return employeeService.createEmployee(employeeDTO);
    }

    @DeleteMapping("/{employeeId}")
    public void deleteEmployee(@PathVariable("employeeId") @Positive(message = "Id must be greater than 0") @NotNull Long id) {
        employeeService.deleteEmployee(id);
    }

    @PutMapping("/{employeeId}")
    public void updateEmployee(@PathVariable("employeeId") @Positive(message = "Id must be greater than 0") @NotNull(message = "Id couldn't be empty") Long id,
                               @RequestParam(value = "email", required = false) @Pattern(regexp = "\\w+@\\w+\\.\\w+",message = "Email doesn't match the form") String email,
                               @RequestParam(value = "salary", required = false) @DecimalMin(value = "5000", message = "Salary must be bigger than 5000") BigDecimal salary,
                               @RequestParam(value = "departmentId", required = false) @Positive(message = "Id must be greater than 0") @NotNull(message = "Id couldn't be empty") Long departmentId) {
        employeeService.updateEmployee(id, email, salary, departmentId);
    }
}
