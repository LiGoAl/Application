package com.example.springbootguide.configuration;

import com.example.springbootguide.model.Department;
import com.example.springbootguide.model.Employee;
import com.example.springbootguide.repository.DepartmentRepository;
import com.example.springbootguide.repository.EmployeeRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Configuration
public class EmployeeDepartmentConfig {
    @Bean
    CommandLineRunner commandLineRunner(EmployeeRepository employeeRepository, DepartmentRepository departmentRepository) {
        return (args) -> {
            var departmentList = List.of(new Department(null, "Department 1"),
                    new Department(null, "Department 2"));
            departmentRepository.saveAll(departmentList);
            var employeeList = List.of(
                    new Employee(null,
                            "Vasya",
                            "vasya@gmail.com",
                            LocalDate.of(2000, 3, 10),
                            new BigDecimal(5000)),
                    new Employee(null,
                            "Pasha",
                            "pasha@gmail.com",
                            LocalDate.of(2002, 3, 10),
                            new BigDecimal(20000)),
                    new Employee(null,
                            "Alex",
                            "alex@gmail.com",
                            LocalDate.of(2001, 3, 10),
                            new BigDecimal(8000))

            );
            employeeList.get(0).setDepartment(departmentList.get(0));
            employeeList.get(1).setDepartment(departmentList.get(0));
            employeeList.get(2).setDepartment(departmentList.get(1));
            employeeRepository.saveAll(employeeList);
        };
    }
}
