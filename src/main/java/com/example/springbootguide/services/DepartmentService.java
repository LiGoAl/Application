package com.example.springbootguide.services;

import com.example.springbootguide.DTO.DepartmentDTO;
import com.example.springbootguide.exceptions.ResourceAlreadyOccupiedException;
import com.example.springbootguide.exceptions.ResourceNotFoundException;
import com.example.springbootguide.mappers.DepartmentMapper;
import com.example.springbootguide.models.Department;
import com.example.springbootguide.repositories.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DepartmentService {
    private final DepartmentRepository departmentRepository;

    public List<DepartmentDTO> getDepartments(Integer page, Integer size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        List<Department> departments = departmentRepository.findAll(pageRequest).getContent();
        return departments.stream()
                .map(DepartmentMapper.INSTANCE::departmentToDepartmentDTO)
                .collect(Collectors.toList());
    }

    public DepartmentDTO createDepartment(DepartmentDTO departmentDTO) {
        Department department = DepartmentMapper.INSTANCE
                .departmentDTOToDepartment(validatedDepartmentDTO(departmentDTO));
        Department savedDepartment = departmentRepository.save(department);
        return DepartmentMapper.INSTANCE.departmentToDepartmentDTO(savedDepartment);
    }


    public void deleteDepartment(Long id) {
        departmentRepository.deleteById(validatedDepartmentId(id).getId());
    }

    @Transactional
    public void updateDepartment(Long id, String name) {
        departmentRepository
                .findById(id)
                .map(department -> validatedDepartmentName(department, name).setName(name))
                .orElseThrow(() -> new ResourceNotFoundException("Department not found by id=%s".formatted(id)));
    }

    private DepartmentDTO validatedDepartmentDTO(DepartmentDTO departmentDTO) {
        if (departmentDTO.getDepartmentId() != null) {
            throw new IllegalArgumentException("Id must be empty");
        }
        if (departmentRepository.findByName(departmentDTO.getDepartmentName()).isPresent()) {
            throw new ResourceAlreadyOccupiedException("Name already taken");
        }

        return departmentDTO;
    }

    public Department validatedDepartmentId(Long id) {
        return departmentRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found by id=%s".formatted(id)));
    }

    private Department validatedDepartmentName(Department department, String name) {
        if (name != null && departmentRepository.findByName(name).isPresent()) {
            throw new ResourceAlreadyOccupiedException("Name already taken");
        } else return department;
    }
}
