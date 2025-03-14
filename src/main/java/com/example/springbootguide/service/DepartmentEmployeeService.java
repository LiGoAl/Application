package com.example.springbootguide.service;

import com.example.springbootguide.DTO.DepartmentDTO;
import com.example.springbootguide.DTO.EmployeeDepartmentDTO;
import com.example.springbootguide.model.Department;
import com.example.springbootguide.model.Employee;
import com.example.springbootguide.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.util.List;

@Service
@Validated
public class DepartmentEmployeeService {
    private final EmployeeRepository employeeRepository;

    @Autowired
    public DepartmentEmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public List<EmployeeDepartmentDTO> getAllEmployeesWithDepartments() {
        return employeeRepository.findAllEmployeesWithDepartments();
    }

    public Department getDepartmentsWithMostEmployees() {
        return employeeRepository.findDepartmentWithMostEmployees();
    }

    public List<DepartmentDTO> getTop10DepartmentsByAverageSalary() {
        return employeeRepository.findTop10DepartmentsByAverageSalary();
    }

    public Department getDepartmentsWithMinimalEmployees() {
        return employeeRepository.findDepartmentWithMinimalEmployees();
    }

    public List<Employee> getEmployeesBySalaryGreaterThan(BigDecimal salary) {
        return employeeRepository.findEmployeesBySalaryGreaterThan(salary);
    }
}
