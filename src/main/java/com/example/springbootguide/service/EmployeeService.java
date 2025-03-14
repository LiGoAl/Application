package com.example.springbootguide.service;

import com.example.springbootguide.model.Department;
import com.example.springbootguide.model.Employee;
import com.example.springbootguide.repository.DepartmentRepository;
import com.example.springbootguide.repository.EmployeeRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Validated
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;

    @Autowired
    public EmployeeService(EmployeeRepository employeeRepository, DepartmentRepository departmentRepository) {
        this.employeeRepository = employeeRepository;
        this.departmentRepository = departmentRepository;
    }

    public List<Employee> getEmployees() {
        return employeeRepository.findAll();
    }

    public Employee createEmployee(@Valid Employee employee) {
        if (employee.getId() != null) {
            throw new IllegalArgumentException("Id must be empty");
        }
        if (employeeRepository.findByEmail(employee.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email alredy taken");
        }
        if (employee.getDepartment() == null) {
            throw new IllegalArgumentException("Department must not be null");
        }
        Optional<Department> dep = departmentRepository.findById(employee.getDepartment().getId());
        if (dep.isEmpty()) {
            throw new IllegalArgumentException("Department does not exist");
        }
        employee.setDepartment(dep.get());
        return employeeRepository.save(employee);
    }


    public void deleteEmployee(@Positive(message = "Id must be greater than 0") @NotNull Long id) {
        if (employeeRepository.findById(id).isEmpty()) {
            throw new IllegalArgumentException("Employee not found by id=%s".formatted(id));
        }
        employeeRepository.deleteById(id);
    }

    @Transactional
    public void updateEmployee(@Positive(message = "Id must be greater than 0") @NotNull(message = "Id couldn't be empty") Long id,
                               @Pattern(regexp = "\\w+@\\w+\\.\\w+",message = "Email doesn't match the form") String email,
                               @DecimalMin(value = "5000", message = "Salary must be bigger than 5000") BigDecimal salary,
                               @Valid Department department) {
        var employee = employeeRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("Employee not found by id=%s".formatted(id)));
        if (email != null && !employee.getEmail().equals(email)) {
            Optional<Employee> employeeOptional = employeeRepository.findByEmail(email);
            if (employeeOptional.isPresent()) {
                throw new IllegalArgumentException("Email alredy taken");
            }
            employee.setEmail(email);
        }
        if (salary != null) {
            if (!salary.equals(employee.getSalary())) {
                employee.setSalary(salary);
            }
        }
        Optional<Department> dep = departmentRepository.findById(department.getId());
        if (dep.isEmpty()) {
            throw new IllegalArgumentException("Department does not exist");
        }
        employee.setDepartment(dep.get());
    }
}
