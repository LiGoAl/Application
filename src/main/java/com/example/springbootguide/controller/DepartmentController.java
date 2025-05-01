package com.example.springbootguide.controller;

import com.example.springbootguide.DTO.DepartmentDTO;
import com.example.springbootguide.exception.RequestValidationException;
import com.example.springbootguide.service.DepartmentService;
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
    public List<DepartmentDTO> readDepartments() {
        return departmentService.getDepartments();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DepartmentDTO createDepartment(@RequestBody @Valid DepartmentDTO departmentDTO) {
        return departmentService.createDepartment(departmentDTO);
    }

    @DeleteMapping("/{departmentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDepartment(@PathVariable("departmentId") Long id) {
        if (id == null || id <= 0) {
            throw new RequestValidationException("Id must be greater than 0 and can't be empty");
        }
        departmentService.deleteDepartment(id);
    }

    @PutMapping("/{departmentId}")
    public void updateDepartment(@PathVariable("departmentId") Long id,
                                 @RequestParam(value = "departmentName", required = false) String name) {
        if (id == null || id <= 0) {
            throw new RequestValidationException("Id must be greater than 0 and can't be empty");
        }
        if (name == null || name.isEmpty()) {
            throw new RequestValidationException("Name can't be empty");
        }
        departmentService.updateDepartment(id, name);
    }
}
