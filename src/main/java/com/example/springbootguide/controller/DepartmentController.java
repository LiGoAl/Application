package com.example.springbootguide.controller;

import com.example.springbootguide.DTO.DepartmentDTO;
import com.example.springbootguide.service.DepartmentService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
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
    public DepartmentDTO createDepartment(@RequestBody @Valid DepartmentDTO departmentDTO) {
        return departmentService.createDepartment(departmentDTO);
    }

    @DeleteMapping("/{departmentId}")
    public void deleteDepartment(@PathVariable("departmentId") @Positive(message = "Id must be greater than 0") @NotNull Long id) {
        departmentService.deleteDepartment(id);
    }

    @PutMapping("/{departmentId}")
    public void updateDepartment(@PathVariable("departmentId") @Positive(message = "Id must be greater than 0") @NotNull Long id,
                                 @RequestParam(value = "name", required = false) @NotBlank String name) {
        departmentService.updateDepartment(id, name);
    }
}
