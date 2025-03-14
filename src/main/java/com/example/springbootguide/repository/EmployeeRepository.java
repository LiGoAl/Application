package com.example.springbootguide.repository;

import com.example.springbootguide.DTO.DepartmentDTO;
import com.example.springbootguide.DTO.EmployeeDepartmentDTO;
import com.example.springbootguide.model.Department;
import com.example.springbootguide.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Optional<Employee> findByEmail(String email);

    @Query("SELECT new com.example.springbootguide.DTO.EmployeeDepartmentDTO(e.id, e.name, d.id, d.name)" +
            "FROM Employee e JOIN e.department d")
    List<EmployeeDepartmentDTO> findAllEmployeesWithDepartments();

    @Query("SELECT e FROM Employee e ORDER BY e.salary DESC")
    List<Employee> findEmployeesBySalaryGreaterThan();

    default List<Employee> findEmployeesBySalaryGreaterThan(BigDecimal salary) {
        List<Employee> employeesWithSalaryGreater = new ArrayList<>(), employees = findEmployeesBySalaryGreaterThan();
        for (Employee employee : employees) {
            if (employee.getSalary().compareTo(salary) > 0) {
                employeesWithSalaryGreater.add(employee);
            }
        }
        return employeesWithSalaryGreater;
    }

    @Query("SELECT d FROM Department d ORDER BY SIZE(d.employees) DESC")
    List<Department> findAllOrderedByEmployeeCount();

    default Department findDepartmentWithMostEmployees() {
        List<Department> departments = findAllOrderedByEmployeeCount();
        return departments.isEmpty() ? null : departments.getFirst();
    }

    default Department findDepartmentWithMinimalEmployees() {
        List<Department> departments = findAllOrderedByEmployeeCount();
        return departments.isEmpty() ? null : departments.getLast();
    }

    @Query("SELECT new com.example.springbootguide.DTO.DepartmentDTO(d.id, d.name, SIZE(d.employees), AVG(e.salary))" +
            "FROM Department d JOIN d.employees e GROUP BY d ORDER BY AVG(e.salary) DESC")
    List<DepartmentDTO> findTopDepartmentsByAverageSalary();

    default List<DepartmentDTO> findTop10DepartmentsByAverageSalary() {
        List<DepartmentDTO> departments0 = findTopDepartmentsByAverageSalary(), departments = new ArrayList<>();
        for (int i = 0; i < 9 && i < departments0.size(); i++) {
            departments.add(departments0.get(i));
        }
        return departments;
    }
}
