package com.example.springbootguide.controllers;

import com.example.springbootguide.DTO.DepartmentDTO;
import com.example.springbootguide.exceptions.RequestValidationException;
import com.example.springbootguide.services.DepartmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/departments")
@RequiredArgsConstructor
@Validated
public class DepartmentController {

    private final DepartmentService departmentService;

    @GetMapping
    public List<DepartmentDTO> readDepartments(@RequestParam(defaultValue = "0") Integer page,
                                               @RequestParam(defaultValue = "5") Integer size) {
        return departmentService.getDepartments(page, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DepartmentDTO createDepartment(@RequestBody @Valid DepartmentDTO departmentDTO) {
        return departmentService.createDepartment(departmentDTO);
    }

    @DeleteMapping("/{departmentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDepartment(@PathVariable("departmentId") Long id) {
        departmentService.deleteDepartment(validatedDepartmentId(id));
    }

    @PutMapping("/{departmentId}")
    public void updateDepartment(@PathVariable("departmentId") Long id,
                                 @RequestParam(value = "departmentName", required = false) String name) {
        departmentService.updateDepartment(validatedDepartmentId(id), validatedDepartmentName(name));
    }

    private Long validatedDepartmentId(Long id) {
        if (id == null || id <= 0) {
            throw new RequestValidationException("Id must be greater than 0 and can't be empty");
        } else return id;
    }

    private String validatedDepartmentName(String name) {
        if (name != null && name.isEmpty()) {
            throw new RequestValidationException("Name can't be empty");
        } else return name;
    }
}
