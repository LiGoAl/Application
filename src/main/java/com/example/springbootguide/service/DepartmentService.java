package com.example.springbootguide.service;

import com.example.springbootguide.DTO.DepartmentDTO;
import com.example.springbootguide.exception.ResourceAlreadyOccupiedException;
import com.example.springbootguide.exception.ResourceNotFoundException;
import com.example.springbootguide.model.Department;
import com.example.springbootguide.model.Employee;
import com.example.springbootguide.repository.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DepartmentService {
    private final DepartmentRepository departmentRepository;

    public List<DepartmentDTO> getDepartments() {
        List<Department> departments = departmentRepository.findAll();
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

    public DepartmentDTO createDepartment(DepartmentDTO departmentDTO) {
        if (departmentDTO.getDepartmentId() != null) {
            throw new IllegalArgumentException("Id must be empty");
        }
        if (departmentRepository.findByName(departmentDTO.getDepartmentName()).isPresent()) {
            throw new ResourceAlreadyOccupiedException("Name already taken");
        }
        Department department = new Department(departmentDTO.getDepartmentId(), departmentDTO.getDepartmentName());
        Department savedDepartment = departmentRepository.save(department);
        if (savedDepartment.getEmployees() != null) {
            return new DepartmentDTO(savedDepartment.getId(), savedDepartment.getName(), savedDepartment.getEmployees().size(),
                    savedDepartment.getEmployees().stream().map(Employee::getSalary).reduce(BigDecimal.ZERO, BigDecimal::add)
                            .divide(BigDecimal.valueOf(savedDepartment.getEmployees().size()), 2, RoundingMode.CEILING));
        } else {
            return new DepartmentDTO(savedDepartment.getId(), savedDepartment.getName(), 0, BigDecimal.ZERO);
        }
    }


    public void deleteDepartment(Long id) {
        if (departmentRepository.findById(id).isEmpty()) {
            throw new ResourceNotFoundException("Department not found by id=%s".formatted(id));
        }
        departmentRepository.deleteById(id);
    }

    @Transactional
    public void updateDepartment(Long id, String name) {
        if (departmentRepository.findByName(name).isPresent()) {
            throw new ResourceAlreadyOccupiedException("Name already taken");
        }
        if (departmentRepository.findById(id).isPresent()) {
            Department department = departmentRepository.findById(id).get();
            department.setName(name);
        } else throw new ResourceNotFoundException("Department not found by id=%s".formatted(id));
    }
}
