package com.example.springbootguide.service;

import com.example.springbootguide.DTO.DepartmentDTO;
import com.example.springbootguide.DTO.EmployeeDTO;
import com.example.springbootguide.DTO.EmployeeDepartmentDTO;
import com.example.springbootguide.model.Department;
import com.example.springbootguide.model.Employee;
import com.example.springbootguide.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DepartmentEmployeeService {
    private final EmployeeRepository employeeRepository;

    public List<EmployeeDepartmentDTO> getAllEmployeesWithDepartments() {
        List<Employee> employees = employeeRepository.findAll();
        List<EmployeeDepartmentDTO> dtos = new ArrayList<>();
        for (Employee employee : employees) {
            dtos.add(new EmployeeDepartmentDTO(employee.getId(), employee.getName(), employee.getDepartment().getId(), employee.getDepartment().getName()));
        }
        return dtos;
    }

    public DepartmentDTO getDepartmentsWithMostEmployees() {
        Department department = employeeRepository.findDepartmentWithMostEmployees();
        if (department.getEmployees() != null) {
            return new DepartmentDTO(department.getId(), department.getName(), department.getEmployees().size(),
                    department.getEmployees().stream().map(Employee::getSalary).reduce(BigDecimal.ZERO, BigDecimal::add)
                            .divide(BigDecimal.valueOf(department.getEmployees().size()), 2, RoundingMode.CEILING));
        } else {
            return new DepartmentDTO(department.getId(), department.getName(), 0, BigDecimal.ZERO);
        }
    }

    public List<DepartmentDTO> getTop10DepartmentsByAverageSalary() {
        List<Department> departments = employeeRepository.findTop10DepartmentsByAverageSalary();
        List<DepartmentDTO> dtos = new ArrayList<>();
        for (Department department : departments) {
            if (department.getEmployees() != null) {
                dtos.add(new DepartmentDTO(department.getId(), department.getName(), department.getEmployees().size(),
                        department.getEmployees().stream().map(Employee::getSalary).reduce(BigDecimal.ZERO, BigDecimal::add)
                                .divide(BigDecimal.valueOf(department.getEmployees().size()), 2, RoundingMode.CEILING)));
            } else {
                dtos.add(new DepartmentDTO(department.getId(), department.getName(), 0, BigDecimal.ZERO));
            }
        }
        return dtos;
    }

    public DepartmentDTO getDepartmentsWithMinimalEmployees() {
        Department department = employeeRepository.findDepartmentWithMinimalEmployees();
        if (department.getEmployees() != null) {
            return new DepartmentDTO(department.getId(), department.getName(), department.getEmployees().size(),
                    department.getEmployees().stream().map(Employee::getSalary).reduce(BigDecimal.ZERO, BigDecimal::add)
                            .divide(BigDecimal.valueOf(department.getEmployees().size()), 2, RoundingMode.CEILING));
        } else {
            return new DepartmentDTO(department.getId(), department.getName(), 0, BigDecimal.ZERO);
        }
    }

    public List<EmployeeDTO> getEmployeesBySalaryGreaterThan(BigDecimal salary) {
        List<Employee> employees = employeeRepository.findEmployeesBySalaryGreaterThan(salary);
        List<EmployeeDTO> dtos = new ArrayList<>();
        for (Employee employee : employees) {
            dtos.add(new EmployeeDTO(employee.getId(), employee.getName(), employee.getEmail(), employee.getBirthDate(),
                    employee.getSalary(), employee.getDepartment().getId()));
        }
        return dtos;
    }
}
