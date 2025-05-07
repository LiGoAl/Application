package com.example.springbootguide.services;

import com.example.springbootguide.DTO.EmployeeDTO;
import com.example.springbootguide.exceptions.ResourceAlreadyOccupiedException;
import com.example.springbootguide.exceptions.ResourceNotFoundException;
import com.example.springbootguide.mappers.EmployeeMapper;
import com.example.springbootguide.models.Employee;
import com.example.springbootguide.repositories.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentService departmentService;

    public List<EmployeeDTO> getEmployees(Integer page, Integer size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        List<Employee> employees = employeeRepository.findAll(pageRequest).getContent();
        return employees.stream()
                .map(EmployeeMapper.INSTANCE::employeeToEmployeeDTO)
                .collect(Collectors.toList());
    }

    public EmployeeDTO createEmployee(EmployeeDTO employeeDTO) {
        Employee employee = EmployeeMapper.INSTANCE.employeeDTOToEmployee(validatedEmployeeDTO(employeeDTO));
        employee.setDepartment(departmentService.validatedDepartmentId(employeeDTO.getDepartmentId()));
        Employee employeeFromBd = employeeRepository.save(employee);
        return EmployeeMapper.INSTANCE.employeeToEmployeeDTO(employeeFromBd);
    }


    public void deleteEmployee(Long id) {
        employeeRepository.deleteById(validatedEmployeeId(id).getId());
    }

    @Transactional
    public void updateEmployee(Long id, String email, BigDecimal salary, Long departmentId) {
        employeeRepository
                .findById(id)
                .map(employee -> validatedEmployee(employee, email, salary, departmentId))
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found by id=%s".formatted(id)));
    }

    private EmployeeDTO validatedEmployeeDTO(EmployeeDTO employeeDTO) {
        if (employeeDTO.getId() != null) {
            throw new IllegalArgumentException("Id must be empty");
        }
        if (employeeRepository.findByEmail(employeeDTO.getEmail()).isPresent()) {
            throw new ResourceAlreadyOccupiedException("Email already taken");
        }
        if (employeeDTO.getDepartmentId() == null) {
            throw new IllegalArgumentException("Department id must not be null");
        }

        return employeeDTO;
    }

    private Employee validatedEmployee(Employee employee, String email, BigDecimal salary, Long departmentId) {
        if (email != null) employee.setEmail(validatedEmployeeEmail(email, employee));
        if (salary != null) employee.setSalary(validatedEmployeeSalary(salary, employee));
        if (departmentId != null) employee.setDepartment(departmentService.validatedDepartmentId(departmentId));
        return employee;
    }

    private Employee validatedEmployeeId(Long id) {
        return employeeRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found by id=%s".formatted(id)));
    }

    private String validatedEmployeeEmail(String email, Employee employee) {
        if (!employee.getEmail().equals(email)) {
            Optional<Employee> employeeOptional = employeeRepository.findByEmail(email);
            if (employeeOptional.isPresent()) {
                throw new ResourceAlreadyOccupiedException("Email already taken");
            }
        }
        return email;
    }

    private BigDecimal validatedEmployeeSalary(BigDecimal salary, Employee employee) {
        if (!salary.equals(employee.getSalary())) {
            return salary;
        } else throw new IllegalArgumentException("The replaced salary haven't been equal to original");
    }
}
