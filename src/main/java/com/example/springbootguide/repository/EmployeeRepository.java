package com.example.springbootguide.repository;

import com.example.springbootguide.model.Department;
import com.example.springbootguide.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Optional<Employee> findByEmail(String email);

    @Query("SELECT e FROM Employee e WHERE e.salary > ?1 ORDER BY e.salary DESC")
    List<Employee> findEmployeesBySalaryGreaterThan(BigDecimal salary);

    @Query(value = "SELECT * FROM department d ORDER BY (SELECT COUNT(*) FROM employee e WHERE e.department_id = d.id) DESC, d.id ASC LIMIT 1", nativeQuery = true)
    Department findDepartmentWithMostEmployees();

    @Query(value = "SELECT * FROM department d ORDER BY (SELECT COUNT(*) FROM employee e WHERE e.department_id = d.id), d.id ASC LIMIT 1", nativeQuery = true)
    Department findDepartmentWithMinimalEmployees();

    @Query(value = "SELECT d.*, AVG(e.salary) AS avg_salary FROM department d " +
            "LEFT JOIN employee e ON e.department_id = d.id GROUP BY d.id " +
            "ORDER BY avg_salary DESC, d.id ASC LIMIT 10", nativeQuery = true)
    List<Department> findTop10DepartmentsByAverageSalary();
}
