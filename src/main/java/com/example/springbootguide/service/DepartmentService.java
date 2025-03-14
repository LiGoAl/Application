package com.example.springbootguide.service;

import com.example.springbootguide.model.Department;
import com.example.springbootguide.model.Employee;
import com.example.springbootguide.repository.DepartmentRepository;
import com.example.springbootguide.repository.EmployeeRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Optional;

@Service
@Validated
public class DepartmentService {
    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;

    @Autowired
    public DepartmentService(DepartmentRepository departmentRepository, EmployeeRepository employeeRepository) {
        this.departmentRepository = departmentRepository;
        this.employeeRepository = employeeRepository;
    }

    public List<Department> getDepartments() {
        return departmentRepository.findAll();
    }

    public Department createDepartment(@Valid Department department) {
        for (@Valid Employee employee : department.getEmployees()) {
            if (employee.getId() != null) {
                throw new IllegalArgumentException("Id must be empty");
            }
            if (employeeRepository.findByEmail(employee.getEmail()).isPresent()) {
                throw new IllegalArgumentException("Email alredy taken");
            }
            employee.setDepartment(department);
        }
        if (department.getId() != null) {
            throw new IllegalArgumentException("Id must be empty");
        }
        if (departmentRepository.findByName(department.getName()).isPresent()) {
            throw new IllegalArgumentException("Name alredy taken");
        }
        return departmentRepository.save(department);
    }


    public void deleteDepartment(@Positive(message = "Id must be greater than 0") @NotNull Long id) {
        if (departmentRepository.findById(id).isEmpty()) {
            throw new IllegalArgumentException("Employee not found by id=%s".formatted(id));
        }
        departmentRepository.deleteById(id);
    }

    @Transactional
    public void updateDepartment(@Positive(message = "Id must be greater than 0") @NotNull(message = "Id couldn't be empty") Long id,
                               List<Employee> employees) {
        for (@Valid Employee employee0 : employees) {
            if (employee0.getId() != null) {
                var employee = employeeRepository.findById(id).orElseThrow(
                        () -> new IllegalArgumentException("Employee not found by id=%s".formatted(id)));
                if (employee0.getEmail() != null && !employee.getEmail().equals(employee0.getEmail())) {
                    Optional<Employee> employeeOptional = employeeRepository.findByEmail(employee0.getEmail());
                    if (employeeOptional.isPresent()) {
                        throw new IllegalArgumentException("Email alredy taken");
                    }
                    employee.setEmail(employee0.getEmail());
                }
                if (employee0.getSalary() != null) {
                    if (!employee0.getSalary().equals(employee.getSalary())) {
                        employee.setSalary(employee0.getSalary());
                    }
                }
            } else {
                if (employeeRepository.findByEmail(employee0.getEmail()).isPresent()) {
                    throw new IllegalArgumentException("Email alredy taken");
                }
                Optional<Department> dep = departmentRepository.findById(id);
                if (dep.isEmpty()) {
                    throw new IllegalArgumentException("Department does not exist");
                }
                employee0.setDepartment(dep.get());
                employeeRepository.save(employee0);
            }
        }
    }
}
