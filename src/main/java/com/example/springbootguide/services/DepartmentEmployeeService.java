package com.example.springbootguide.services;

import com.example.springbootguide.DTO.DepartmentDTO;
import com.example.springbootguide.DTO.EmployeeDTO;
import com.example.springbootguide.DTO.EmployeeDepartmentDTO;
import com.example.springbootguide.mappers.DepartmentMapper;
import com.example.springbootguide.mappers.EmployeeDepartmentMapper;
import com.example.springbootguide.mappers.EmployeeMapper;
import com.example.springbootguide.models.Department;
import com.example.springbootguide.models.Employee;
import com.example.springbootguide.repositories.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DepartmentEmployeeService {
    private final EmployeeRepository employeeRepository;

    public List<EmployeeDepartmentDTO> getAllEmployeesWithDepartments(Integer page, Integer size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        List<Employee> employees = employeeRepository.findAll(pageRequest).getContent();
        return employees.stream()
                .map(EmployeeDepartmentMapper.INSTANCE::employeeToEmployeeDepartmentDTO)
                .collect(Collectors.toList());
    }

    public DepartmentDTO getDepartmentWithMostEmployees() {
        Optional<Department> departmentFromBD = employeeRepository.findDepartmentWithMostEmployees();
        return departmentFromBD
                .map(DepartmentMapper.INSTANCE::departmentToDepartmentDTO)
                .orElse(null);
    }

    public List<DepartmentDTO> getTop10DepartmentsByAverageSalary() {
        List<Department> departments = employeeRepository.findTop10DepartmentsByAverageSalary();
        return departments.stream()
                .map(DepartmentMapper.INSTANCE::departmentToDepartmentDTO)
                .collect(Collectors.toList());
    }

    public DepartmentDTO getDepartmentWithMinimalEmployees() {
        Optional<Department> departmentFromBD = employeeRepository.findDepartmentWithMinimalEmployees();
        return departmentFromBD
                .map(DepartmentMapper.INSTANCE::departmentToDepartmentDTO)
                .orElse(null);
    }

    public List<EmployeeDTO> getEmployeesBySalaryGreaterThan(BigDecimal salary, Integer page, Integer size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        List<Employee> employees = employeeRepository.findEmployeesBySalaryGreaterThan(salary, pageRequest).getContent();
        return employees.stream()
                .map(EmployeeMapper.INSTANCE::employeeToEmployeeDTO)
                .collect(Collectors.toList());
    }
}
