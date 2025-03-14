package com.example.springbootguide.controller;

import com.example.springbootguide.model.Department;
import com.example.springbootguide.model.Employee;
import com.example.springbootguide.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/departments")
public class DepartmentController {

    private final DepartmentService departmentService;

    @Autowired
    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @GetMapping
    public List<Department> readDepartments() {
        return departmentService.getDepartments();
    }

    @PostMapping
    public Department createDepartment(@RequestBody Department department) {
        return departmentService.createDepartment(department);
    }

    @DeleteMapping("/{departmentId}")
    public void deleteDepartment(@PathVariable("departmentId") Long id) {
        departmentService.deleteDepartment(id);
    }

    @PutMapping("/{departmentId}/{employees}")
    public void updateDepartment(@PathVariable("departmentId") Long id, @RequestBody List<Employee> employees) {
        departmentService.updateDepartment(id, employees);
    }
}
