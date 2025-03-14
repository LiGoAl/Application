package com.example.springbootguide.controller;

import com.example.springbootguide.model.Department;
import com.example.springbootguide.model.Employee;
import com.example.springbootguide.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    @Autowired
    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping
    public List<Employee> readEmployees() {
        return employeeService.getEmployees();
    }

    @PostMapping
    public Employee createEmployee(@RequestBody Employee employee) {
        return employeeService.createEmployee(employee);
    }

    @DeleteMapping("/{employeeId}")
    public void deleteEmployee(@PathVariable("employeeId") Long id) {
        employeeService.deleteEmployee(id);
    }

    @PutMapping("/{employeeId}")
    public void updateEmployee(@PathVariable("employeeId") Long id, @RequestParam(value = "email", required = false) String email,
                               @RequestParam(value = "salary", required = false) BigDecimal salary,
                               @RequestParam(value = "department", required = false) Department department) {
        employeeService.updateEmployee(id, email, salary, department);
    }
}
