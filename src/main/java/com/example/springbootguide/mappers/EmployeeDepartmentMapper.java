package com.example.springbootguide.mappers;

import com.example.springbootguide.DTO.EmployeeDepartmentDTO;
import com.example.springbootguide.models.Employee;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface EmployeeDepartmentMapper {
    EmployeeDepartmentMapper INSTANCE = Mappers.getMapper(EmployeeDepartmentMapper.class);

    @Mapping(source = "id", target = "employeeId")
    @Mapping(source = "name", target = "employeeName")
    @Mapping(source = "department.id", target = "departmentId")
    @Mapping(source = "department.name", target = "departmentName")
    EmployeeDepartmentDTO employeeToEmployeeDepartmentDTO(Employee employee);
}