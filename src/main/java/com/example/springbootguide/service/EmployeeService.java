package com.example.springbootguide.service;

import com.example.springbootguide.DTO.EmployeeDTO;
import com.example.springbootguide.exception.ResourceAlreadyOccupiedException;
import com.example.springbootguide.exception.ResourceNotFoundException;
import com.example.springbootguide.model.Department;
import com.example.springbootguide.model.Employee;
import com.example.springbootguide.repository.DepartmentRepository;
import com.example.springbootguide.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;

    public List<EmployeeDTO> getEmployees() {
        List<Employee> employees = employeeRepository.findAll();
        List<EmployeeDTO> dtos = new ArrayList<>();
        for (Employee employee : employees) {
            dtos.add(new EmployeeDTO(employee.getId(), employee.getName(), employee.getEmail(),
                    employee.getBirthDate(), employee.getSalary(), employee.getDepartment().getId()));
        }
        return dtos;
    }

    public EmployeeDTO createEmployee(EmployeeDTO employeeDTO) {
        if (employeeDTO.getId() != null) {
            throw new IllegalArgumentException("Id must be empty");
        }
        if (employeeRepository.findByEmail(employeeDTO.getEmail()).isPresent()) {
            throw new ResourceAlreadyOccupiedException("Email already taken");
        }
        if (employeeDTO.getDepartmentId() == null) {
            throw new IllegalArgumentException("Department id must not be null");
        }
        Optional<Department> dep = departmentRepository.findById(employeeDTO.getDepartmentId());
        if (dep.isEmpty()) {
            throw new ResourceNotFoundException("Department not found by id=%s".formatted(employeeDTO.getDepartmentId()));
        }
        Employee employee = new Employee(employeeDTO.getId(), employeeDTO.getName(), employeeDTO.getEmail(),
                employeeDTO.getBirthDate(), employeeDTO.getSalary());
        employee.setDepartment(dep.get());
        Employee employeeFromBd = employeeRepository.save(employee);
        return new EmployeeDTO(employeeFromBd.getId(), employeeFromBd.getName(), employeeFromBd.getEmail(),
                employeeFromBd.getBirthDate(), employeeFromBd.getSalary(), employeeFromBd.getDepartment().getId());
    }


    public void deleteEmployee(Long id) {
        if (employeeRepository.findById(id).isEmpty()) {
            throw new ResourceNotFoundException("Employee not found by id=%s".formatted(id));
        }
        employeeRepository.deleteById(id);
    }

    @Transactional
    public void updateEmployee(Long id, String email, BigDecimal salary, Long departmentId) {
        var employee = employeeRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Employee not found by id=%s".formatted(id)));
        if (email != null && !employee.getEmail().equals(email)) {
            Optional<Employee> employeeOptional = employeeRepository.findByEmail(email);
            if (employeeOptional.isPresent()) {
                throw new ResourceAlreadyOccupiedException("Email already taken");
            }
            employee.setEmail(email);
        }
        if (salary != null) {
            if (!salary.equals(employee.getSalary())) {
                employee.setSalary(salary);
            }
        }
        Optional<Department> dep = departmentRepository.findById(departmentId);
        if (dep.isEmpty()) {
            throw new ResourceNotFoundException("Department not found by id=%s".formatted(departmentId));
        }
        employee.setDepartment(dep.get());
    }
}
