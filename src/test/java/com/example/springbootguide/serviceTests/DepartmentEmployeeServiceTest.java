package com.example.springbootguide.serviceTests;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.springbootguide.DTO.DepartmentDTO;
import com.example.springbootguide.DTO.EmployeeDTO;
import com.example.springbootguide.DTO.EmployeeDepartmentDTO;
import com.example.springbootguide.model.Department;
import com.example.springbootguide.model.Employee;
import com.example.springbootguide.repository.EmployeeRepository;
import com.example.springbootguide.service.DepartmentEmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DepartmentEmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private DepartmentEmployeeService departmentEmployeeService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private static List<Employee> setUpEmployees() {
        Employee employee1 = new Employee(1L, "Jar", "jar@mail.ru", LocalDate.parse("2000-01-01"), BigDecimal.valueOf(10000));
        Employee employee2 = new Employee(2L, "Rar", "rar@mail.ru", LocalDate.parse("2002-01-01"), BigDecimal.valueOf(20000));
        Department department1 = new Department(1L, "Department 1");
        Department department2 = new Department(2L, "Department 2");
        department1.setEmployees(List.of(employee1));
        department2.setEmployees(List.of(employee2));
        employee1.setDepartment(department1);
        employee2.setDepartment(department2);
        List<Employee> employees = new ArrayList<>();
        employees.add(employee1);
        employees.add(employee2);
        return employees;
    }

    private static List<Department> setUpDepartments() {
        Employee employee1 = new Employee(1L, "Jar", "jar@mail.ru", LocalDate.parse("2000-01-01"), BigDecimal.valueOf(10000));
        Employee employee2 = new Employee(2L, "Rar", "rar@mail.ru", LocalDate.parse("2002-01-01"), BigDecimal.valueOf(20000));
        Department department1 = new Department(1L, "Department 1");
        Department department2 = new Department(2L, "Department 2");
        department1.setEmployees(List.of(employee1));
        department2.setEmployees(List.of(employee2));
        employee1.setDepartment(department1);
        employee2.setDepartment(department2);
        List<Department> departments = new ArrayList<>();
        departments.add(department1);
        departments.add(department2);
        return departments;
    }

    @Test
    public void testGetAllEmployeesWithDepartments_Success() {
        when(employeeRepository.findAll()).thenReturn(setUpEmployees());

        List<EmployeeDepartmentDTO> result = departmentEmployeeService.getAllEmployeesWithDepartments();

        assertEquals(2, result.size());

        for (int i = 0; i < result.size(); i++) {
            assertEquals(setUpEmployees().get(i).getId(), result.get(i).getEmployeeId());
            assertEquals(setUpEmployees().get(i).getName(), result.get(i).getEmployeeName());
            assertEquals(setUpEmployees().get(i).getDepartment().getId(), result.get(i).getDepartmentId());
            assertEquals(setUpEmployees().get(i).getDepartment().getName(), result.get(i).getDepartmentName());
        }

        verify(employeeRepository, times(1)).findAll();
    }

    @Test
    public void testGetDepartmentWithMostEmployees_Success() {
        BigDecimal salary = setUpDepartments().getFirst().getEmployees().stream().map(Employee::getSalary).reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(setUpDepartments().getFirst().getEmployees().size()), 2, RoundingMode.CEILING);

        when(employeeRepository.findDepartmentWithMostEmployees()).thenReturn(setUpDepartments().getFirst());

        DepartmentDTO result = departmentEmployeeService.getDepartmentWithMostEmployees();

        assertEquals(setUpDepartments().getFirst().getId(), result.getDepartmentId());
        assertEquals(setUpDepartments().getFirst().getName(), result.getDepartmentName());
        assertEquals(setUpDepartments().getFirst().getEmployees().size(), result.getEmployeeSize());
        assertEquals(salary, result.getAverageSalary());

        verify(employeeRepository, times(1)).findDepartmentWithMostEmployees();
    }

    @Test
    public void testGetDepartmentWithMinimalEmployees_Success() {
        BigDecimal salary = setUpDepartments().getFirst().getEmployees().stream().map(Employee::getSalary).reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(setUpDepartments().getFirst().getEmployees().size()), 2, RoundingMode.CEILING);

        when(employeeRepository.findDepartmentWithMinimalEmployees()).thenReturn(setUpDepartments().getFirst());

        DepartmentDTO result = departmentEmployeeService.getDepartmentWithMinimalEmployees();

        assertEquals(setUpDepartments().getFirst().getId(), result.getDepartmentId());
        assertEquals(setUpDepartments().getFirst().getName(), result.getDepartmentName());
        assertEquals(setUpDepartments().getFirst().getEmployees().size(), result.getEmployeeSize());
        assertEquals(salary, result.getAverageSalary());

        verify(employeeRepository, times(1)).findDepartmentWithMinimalEmployees();
    }

    @Test
    public void testGetEmployeesBySalaryGreaterThan_Success() {
        BigDecimal salary = BigDecimal.valueOf(7500);

        when(employeeRepository.findEmployeesBySalaryGreaterThan(salary)).thenReturn(setUpEmployees());

        List<EmployeeDTO> result = departmentEmployeeService.getEmployeesBySalaryGreaterThan(salary);

        assertEquals(2, result.size());

        for (int i = 0; i < result.size(); i++) {
            assertEquals(setUpEmployees().get(i).getId(), result.get(i).getId());
            assertEquals(setUpEmployees().get(i).getName(), result.get(i).getName());
            assertEquals(setUpEmployees().get(i).getEmail(), result.get(i).getEmail());
            assertEquals(setUpEmployees().get(i).getSalary(), result.get(i).getSalary());
            assertEquals(setUpEmployees().get(i).getBirthDate(), result.get(i).getBirthDate());
            assertEquals(setUpEmployees().get(i).getDepartment().getId(), result.get(i).getDepartmentId());
        }

        verify(employeeRepository, times(1)).findEmployeesBySalaryGreaterThan(salary);
    }

    @Test
    public void testGetTop10DepartmentsByAverageSalary_Success() {
        List<BigDecimal> salaries = new ArrayList<>();

        for (int i = 0; i < setUpDepartments().size(); i++) {
            salaries.add(setUpDepartments().get(i).getEmployees().stream().map(Employee::getSalary).reduce(BigDecimal.ZERO, BigDecimal::add)
                    .divide(BigDecimal.valueOf(setUpDepartments().get(i).getEmployees().size()), 2, RoundingMode.CEILING));
        }

        when(employeeRepository.findTop10DepartmentsByAverageSalary()).thenReturn(setUpDepartments());

        List<DepartmentDTO> result = departmentEmployeeService.getTop10DepartmentsByAverageSalary();

        assertEquals(2, result.size());

        for (int i = 0; i < result.size(); i++) {
            assertEquals(setUpDepartments().get(i).getId(), result.get(i).getDepartmentId());
            assertEquals(setUpDepartments().get(i).getName(), result.get(i).getDepartmentName());
            assertEquals(setUpDepartments().get(i).getEmployees().size(), result.get(i).getEmployeeSize());
            assertEquals(salaries.get(i), result.get(i).getAverageSalary());
        }

        verify(employeeRepository, times(1)).findTop10DepartmentsByAverageSalary();
    }
}
