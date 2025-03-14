package com.example.springbootguide.controller;

import com.example.springbootguide.DTO.DepartmentDTO;
import com.example.springbootguide.DTO.EmployeeDepartmentDTO;
import com.example.springbootguide.model.Department;
import com.example.springbootguide.model.Employee;
import com.example.springbootguide.service.DepartmentEmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping
public class DepartmentEmployeeController {

    private final DepartmentEmployeeService departmentEmployeeService;

    @Autowired
    public DepartmentEmployeeController(DepartmentEmployeeService departmentEmployeeService) {
        this.departmentEmployeeService = departmentEmployeeService;
    }

    @GetMapping("/with-departments")
    public List<EmployeeDepartmentDTO> getAllEmployeesWithDepartments() {
        return departmentEmployeeService.getAllEmployeesWithDepartments();
    }

    @GetMapping("/most-employees")
    public Department getDepartmentsWithMostEmployees() {
        return departmentEmployeeService.getDepartmentsWithMostEmployees();
    }

    @GetMapping("/minimal-employees")
    public Department getDepartmentsWithMinimalEmployees() {
        return departmentEmployeeService.getDepartmentsWithMinimalEmployees();
    }

    @GetMapping("/salary-greater-than/{salary}")
    public List<Employee> getEmployeesWithSalaryGreaterThan(@PathVariable("salary") BigDecimal salary) {
        return departmentEmployeeService.getEmployeesBySalaryGreaterThan(salary);
    }

    @GetMapping("/top10-department-by-salaries")
    public List<DepartmentDTO> getTop10DepartmentsByAverageSalary() {
        return departmentEmployeeService.getTop10DepartmentsByAverageSalary();
    }
}
