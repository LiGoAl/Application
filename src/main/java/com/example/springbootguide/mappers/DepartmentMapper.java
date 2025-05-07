package com.example.springbootguide.mappers;

import com.example.springbootguide.DTO.DepartmentDTO;
import com.example.springbootguide.models.Department;
import com.example.springbootguide.models.Employee;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Mapper
public interface DepartmentMapper {
    DepartmentMapper INSTANCE = Mappers.getMapper(DepartmentMapper.class);

    @Mapping(source = "id", target = "departmentId")
    @Mapping(source = "name", target = "departmentName")
    @Mapping(target = "employeeSize", expression = "java(department.getEmployees() == null ? 0 : department.getEmployees().size())")
    @Mapping(target = "averageSalary", expression = "java(calculateAverageSalary(department))")
    DepartmentDTO departmentToDepartmentDTO(Department department);

    @Mapping(source = "departmentId", target = "id")
    @Mapping(source = "departmentName", target = "name")
    @Mapping(target = "employees", ignore = true)
    Department departmentDTOToDepartment(DepartmentDTO departmentDTO);

    default BigDecimal calculateAverageSalary(Department department) {
        if (department.getEmployees() == null || department.getEmployees().isEmpty()) {
            return BigDecimal.ZERO;
        }
        return department.getEmployees().stream()
                .map(Employee::getSalary)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(department.getEmployees().size()), 2, RoundingMode.CEILING);
    }
}
