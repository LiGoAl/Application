package com.example.springbootguide.repositoryTests;

import com.example.springbootguide.models.Department;
import com.example.springbootguide.models.Employee;
import com.example.springbootguide.repositories.DepartmentRepository;
import com.example.springbootguide.repositories.EmployeeRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@ActiveProfiles("test")
public class RepositoryTest {

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Test
    public void testFindByName_Success() {
        String name = "HR";

        Optional<Department> foundDepartment = departmentRepository.findByName(name);

        assertThat(foundDepartment).isPresent();
        assertThat(foundDepartment.get().getName()).isEqualTo(name);
    }

    @Test
    public void testFindByName_NotFound() {
        Optional<Department> foundDepartment = departmentRepository.findByName("PIZZA");

        assertThat(foundDepartment).isEmpty();
    }

    @Test
    public void testFindByEmail_Success() {
        String email = "pasha@gmail.com";

        Optional<Employee> foundEmployee = employeeRepository.findByEmail(email);

        assertThat(foundEmployee).isPresent();
        assertThat(foundEmployee.get().getEmail()).isEqualTo(email);
    }

    @Test
    public void testFindByEmail_NotFound() {
        Optional<Employee> foundEmployee = employeeRepository.findByEmail("pasha@test.com");

        assertThat(foundEmployee).isEmpty();
    }

    @Test
    public void testFindEmployeesBySalaryGreaterThan_Success() {
        BigDecimal salary = BigDecimal.valueOf(70000);
        Integer size = 8;

        List<Employee> foundEmployees = employeeRepository.findEmployeesBySalaryGreaterThan(salary, PageRequest.of(0, 10)).getContent();

        assertEquals(size, foundEmployees.size());
        for (Employee employee : foundEmployees) {
            assertEquals(1, employee.getSalary().compareTo(salary));
        }
    }

    @Test
    public void testFindEmployeesBySalaryGreaterThan_NotFound() {
        BigDecimal salary = BigDecimal.valueOf(170000);

        List<Employee> foundEmployees = employeeRepository.findEmployeesBySalaryGreaterThan(salary, PageRequest.of(0, 10)).getContent();

        assertThat(foundEmployees).isEmpty();
    }

    @Test
    public void testFindDepartmentWithMostEmployees_Success() {
        Long id = 6L;
        String name = "Building";
        Integer size = 4;

        Optional<Department> optionalDepartment = employeeRepository.findDepartmentWithMostEmployees();

        assertThat(optionalDepartment).isPresent();
        Department foundDepartment = optionalDepartment.get();

        assertEquals(id, foundDepartment.getId());
        assertEquals(name, foundDepartment.getName());
        assertEquals(size, foundDepartment.getEmployees().size());
    }

    @Test
    public void testFindDepartmentWithMinimalEmployees_Success() {
        Long id = 9L;
        String name = "HR-2";
        Integer size = 1;

        Optional<Department> optionalDepartment = employeeRepository.findDepartmentWithMinimalEmployees();

        assertThat(optionalDepartment).isPresent();
        Department foundDepartment = optionalDepartment.get();

        assertEquals(id, foundDepartment.getId());
        assertEquals(name, foundDepartment.getName());
        assertEquals(size, foundDepartment.getEmployees().size());
    }

    @Test
    public void testFindTop10DepartmentsByAverageSalary_Success() {
        List<String> names = List.of("HR-2", "Dev", "LOL-2", "Building", "Design", "Dev-2", "Dota-2", "LOL", "Design-2", "Dota");

        List<Department> foundDepartments = employeeRepository.findTop10DepartmentsByAverageSalary();

        assertEquals(names.size(), foundDepartments.size());
        for (int i = 0; i < foundDepartments.size(); i++) {
            assertEquals(names.get(i), foundDepartments.get(i).getName());
        }
    }
}

