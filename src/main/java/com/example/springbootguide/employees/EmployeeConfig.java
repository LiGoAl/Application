package com.example.springbootguide.employees;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.util.List;

@Configuration
public class EmployeeConfig {
    @Bean
    CommandLineRunner commandLineRunner(EmployeeRepository employeeRepository) {
        return (args) -> {
            var employeeList = List.of(
                    new Employee(null,
                            "Vasya",
                            "vasya@gmail.com",
                            LocalDate.of(2000, 3, 10),
                            10000),
                    new Employee(null,
                            "Pasha",
                            "pasha@gmail.com",
                            LocalDate.of(2002, 3, 10),
                            20000)
            );
            employeeRepository.saveAll(employeeList);
        };
    }
}
