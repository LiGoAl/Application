package com.example.springbootguide.mappers;

import com.example.springbootguide.DTO.EmployeeDTO;
import com.example.springbootguide.models.Employee;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface EmployeeMapper {
    EmployeeMapper INSTANCE = Mappers.getMapper(EmployeeMapper.class);

    @Mapping(source = "department.id", target = "departmentId")
    @Mapping(target = "age", ignore = true)
    EmployeeDTO employeeToEmployeeDTO(Employee employee);

    @Mapping(target = "department", ignore = true)
    Employee employeeDTOToEmployee(EmployeeDTO employeeDTO);
}
