package com.example.springbootguide.employees;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    @Autowired
    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public List<Employee> getEmployees() {
        return employeeRepository.findAll();
    }

    public Employee createEmployee(Employee employee) {
        if (employee.getId() != null) {
            throw new IllegalArgumentException("Id must be empty");
        }
        if (employeeRepository.findByEmail(employee.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email alredy taken");
        }
        if (employee.getSalary() <= 5000) {
            throw new IllegalArgumentException("Salary must be bigger than 5000");
        }
        return employeeRepository.save(employee);
    }


    public void deleteEmployee(Long id) {
        if (employeeRepository.findById(id).isEmpty()) {
            throw new IllegalArgumentException("Employee not found by id=%s".formatted(id));
        }
        employeeRepository.deleteById(id);
    }

    @Transactional
    public void updateEmployee(Long id, String email, Integer salary) {
        var employee = employeeRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("Employee not found by id=%s".formatted(id)));
        if (email != null && !email.isEmpty() && !employee.getEmail().equals(email)) {
            Optional<Employee> employeeOptional = employeeRepository.findByEmail(email);
            if (employeeOptional.isPresent()) {
                throw new IllegalArgumentException("Email alredy taken");
            }
            employee.setEmail(email);
        }
        if (salary != null) {
            if (salary <= 5000) {
                throw new IllegalArgumentException("Salary must be bigger than 5000");
            }
            employee.setSalary(salary);
        }
    }
}
