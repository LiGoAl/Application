package com.example.springbootguide.serviceTests;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.springbootguide.DTO.EmployeeDTO;
import com.example.springbootguide.exceptions.ResourceAlreadyOccupiedException;
import com.example.springbootguide.exceptions.ResourceNotFoundException;
import com.example.springbootguide.models.Department;
import com.example.springbootguide.models.Employee;
import com.example.springbootguide.repositories.EmployeeRepository;
import com.example.springbootguide.services.DepartmentService;
import com.example.springbootguide.services.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;
    @Mock
    private DepartmentService departmentService;

    @InjectMocks
    private EmployeeService employeeService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetEmployees_Success() {
        Employee employee1 = new Employee(1L, "Jar", "jar@mail.ru", LocalDate.parse("2000-01-01"), BigDecimal.valueOf(10000));
        Employee employee2 = new Employee(2L, "Rar", "rar@mail.ru", LocalDate.parse("2002-01-01"), BigDecimal.valueOf(20000));
        Department department1 = new Department(1L, "Department 1");
        Department department2 = new Department(2L, "Department 2");
        department1.setEmployees(List.of(employee1));
        department2.setEmployees(List.of(employee2));
        employee1.setDepartment(department1);
        employee2.setDepartment(department2);

        when(employeeRepository.findAll(any(PageRequest.class))).thenReturn(new PageImpl<>(List.of(employee1, employee2), PageRequest.of(0, 10), 2));

        List<EmployeeDTO> result = employeeService.getEmployees(0, 10);

        assertEquals(2, result.size());

        EmployeeDTO dto1 = result.get(0);
        assertEquals(employee1.getId(), dto1.getId());
        assertEquals(employee1.getName(), dto1.getName());
        assertEquals(employee1.getEmail(), dto1.getEmail());
        assertEquals(employee1.getBirthDate(), dto1.getBirthDate());
        assertEquals(0, dto1.getSalary().compareTo(employee1.getSalary()));
        assertEquals(employee1.getDepartment().getId(), dto1.getDepartmentId());

        EmployeeDTO dto2 = result.get(1);
        assertEquals(employee2.getId(), dto2.getId());
        assertEquals(employee2.getName(), dto2.getName());
        assertEquals(employee2.getEmail(), dto2.getEmail());
        assertEquals(employee2.getBirthDate(), dto2.getBirthDate());
        assertEquals(0, dto2.getSalary().compareTo(employee2.getSalary()));
        assertEquals(employee2.getDepartment().getId(), dto2.getDepartmentId());

        verify(employeeRepository, times(1)).findAll(any(PageRequest.class));
    }

    @Test
    public void testCreateEmployee_Success() {
        EmployeeDTO employeeDTO = new EmployeeDTO(null, "Rar", "rar@mail.ru", LocalDate.parse("2000-01-01"), BigDecimal.valueOf(10000), 1L);
        Employee employee = new Employee(1L, "Rar", "rar@mail.ru", LocalDate.parse("2000-01-01"), BigDecimal.valueOf(10000));
        Department department = new Department(1L, "Department 1");
        employee.setDepartment(department);
        department.setEmployees(List.of(employee));

        when(departmentService.validatedDepartmentId(employeeDTO.getDepartmentId())).thenReturn(department);
        when(employeeRepository.findByEmail(employeeDTO.getEmail())).thenReturn(Optional.empty());
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);

        EmployeeDTO result = employeeService.createEmployee(employeeDTO);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(employeeDTO.getName(), result.getName());
        assertEquals(employeeDTO.getEmail(), result.getEmail());
        assertEquals(employeeDTO.getBirthDate(), result.getBirthDate());
        assertEquals(0, result.getSalary().compareTo(employeeDTO.getSalary()));
        assertEquals(employeeDTO.getDepartmentId(), result.getDepartmentId());

        verify(employeeRepository, times(1)).findByEmail(employeeDTO.getEmail());
        verify(employeeRepository, times(1)).save(any(Employee.class));
        verify(departmentService, times(1)).validatedDepartmentId(employeeDTO.getDepartmentId());
    }

    @Test
    public void testCreateEmployee_IllegalId() {
        EmployeeDTO employeeDTO = new EmployeeDTO(1L, "Rar", "rar@mail.ru", LocalDate.parse("2000-01-01"), BigDecimal.valueOf(10000), 1L);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                employeeService.createEmployee(employeeDTO));

        assertEquals("Id must be empty", exception.getMessage());
    }

    @Test
    public void testCreateEmployee_EmailAlreadyTaken() {
        EmployeeDTO employeeDTO = new EmployeeDTO(null, "Rar", "rar@mail.ru", LocalDate.parse("2000-01-01"), BigDecimal.valueOf(10000), 1L);
        Employee existingEmployee = new Employee(1L, "Rar", "rar@mail.ru", LocalDate.parse("2000-01-01"), BigDecimal.valueOf(10000));
        Department department = new Department(1L, "Department 1");
        existingEmployee.setDepartment(department);
        department.setEmployees(List.of(existingEmployee));

        when(employeeRepository.findByEmail(employeeDTO.getEmail())).thenReturn(Optional.of(existingEmployee));

        ResourceAlreadyOccupiedException exception = assertThrows(ResourceAlreadyOccupiedException.class, () ->
                employeeService.createEmployee(employeeDTO));

        assertEquals("Email already taken", exception.getMessage());

        verify(employeeRepository, times(1)).findByEmail(employeeDTO.getEmail());
    }

    @Test
    public void testCreateEmployee_IllegalDepartmentId() {
        EmployeeDTO employeeDTO = new EmployeeDTO(null, "Rar", "rar24@mail.ru", LocalDate.parse("2000-01-01"), BigDecimal.valueOf(10000), null);

        when(employeeRepository.findByEmail(employeeDTO.getEmail())).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                employeeService.createEmployee(employeeDTO));

        assertEquals("Department id must not be null", exception.getMessage());

        verify(employeeRepository, times(1)).findByEmail(employeeDTO.getEmail());
    }

    @Test
    public void testCreateEmployee_NotFoundDepartment() {
        EmployeeDTO employeeDTO = new EmployeeDTO(null, "Rar", "rar24@mail.ru", LocalDate.parse("2000-01-01"), BigDecimal.valueOf(10000), 2L);

        when(employeeRepository.findByEmail(employeeDTO.getEmail())).thenReturn(Optional.empty());
        when(departmentService.validatedDepartmentId(employeeDTO.getDepartmentId())).thenThrow(new ResourceNotFoundException("Department not found by id=%s".formatted(employeeDTO.getDepartmentId())));

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
                employeeService.createEmployee(employeeDTO));

        assertEquals("Department not found by id=%s".formatted(employeeDTO.getDepartmentId()), exception.getMessage());

        verify(employeeRepository, times(1)).findByEmail(employeeDTO.getEmail());
        verify(departmentService, times(1)).validatedDepartmentId(employeeDTO.getDepartmentId());
    }

    @Test
    public void testDeleteEmployee_Success() {
        Employee employee = new Employee(1L, "Rar", "rar@mail.ru", LocalDate.parse("2000-01-01"), BigDecimal.valueOf(10000));
        Long id = 1L;

        when(employeeRepository.findById(id)).thenReturn(Optional.of(employee));
        doNothing().when(employeeRepository).deleteById(id);

        employeeService.deleteEmployee(id);

        verify(employeeRepository, times(1)).findById(id);
        verify(employeeRepository, times(1)).deleteById(id);
    }

    @Test
    public void testDeleteEmployee_NotFound() {
        Long id = 1L;

        when(employeeRepository.findById(id)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
                employeeService.deleteEmployee(id));

        assertEquals("Employee not found by id=%s".formatted(id), exception.getMessage());

        verify(employeeRepository, times(1)).findById(id);
    }

    @Test
    public void testUpdateEmployee_Success() {
        Employee employee = new Employee(1L, "Rar", "rar@mail.ru", LocalDate.parse("2000-01-01"), BigDecimal.valueOf(10000));
        Department department1 = new Department(1L, "Department 1");
        employee.setDepartment(department1);
        department1.setEmployees(List.of(employee));
        Department department2 = new Department(2L, "Department 2");
        Long id = 1L, departmentId = 2L;
        String email = "hor@gmail.com";
        BigDecimal salary = BigDecimal.valueOf(15000);

        when(employeeRepository.findById(id)).thenReturn(Optional.of(employee));
        when(employeeRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(departmentService.validatedDepartmentId(departmentId)).thenReturn(department2);

        employeeService.updateEmployee(id, email, salary, departmentId);

        assertEquals(departmentId, employee.getDepartment().getId());
        assertEquals(email, employee.getEmail());
        assertEquals(salary, employee.getSalary());

        verify(employeeRepository, times(1)).findById(id);
        verify(employeeRepository, times(1)).findByEmail(email);
        verify(departmentService, times(1)).validatedDepartmentId(departmentId);
    }

    @Test
    public void testUpdateEmployee_NotFoundEmployee() {
        Long id = 1L, departmentId = 2L;
        String email = "hor@gmail.com";
        BigDecimal salary = BigDecimal.valueOf(15000);

        when(employeeRepository.findById(id)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
                employeeService.updateEmployee(id, email, salary, departmentId));

        assertEquals("Employee not found by id=%s".formatted(id), exception.getMessage());

        verify(employeeRepository, times(1)).findById(id);
    }

    @Test
    public void testUpdateEmployee_EmailAlreadyTaken() {
        Employee employee1 = new Employee(1L, "Rar", "rar@mail.ru", LocalDate.parse("2000-01-01"), BigDecimal.valueOf(10000));
        Department department1 = new Department(1L, "Department 1");
        employee1.setDepartment(department1);
        department1.setEmployees(List.of(employee1));
        Employee employee2 = new Employee(1L, "Jar", "hor@gmail.com", LocalDate.parse("2002-01-01"), BigDecimal.valueOf(15000));
        Department department2 = new Department(2L, "Department 2");
        employee2.setDepartment(department2);
        department2.setEmployees(List.of(employee2));
        Long id = 1L, departmentId = 2L;
        String email = "hor@gmail.com";
        BigDecimal salary = BigDecimal.valueOf(15000);

        when(employeeRepository.findById(id)).thenReturn(Optional.of(employee1));
        when(employeeRepository.findByEmail(email)).thenReturn(Optional.of(employee2));

        ResourceAlreadyOccupiedException exception = assertThrows(ResourceAlreadyOccupiedException.class, () ->
                employeeService.updateEmployee(id, email, salary, departmentId));

        assertEquals("Email already taken", exception.getMessage());

        verify(employeeRepository, times(1)).findById(id);
        verify(employeeRepository, times(1)).findByEmail(email);
    }

    @Test
    public void testUpdateEmployee_NotFoundDepartment() {
        Employee employee = new Employee(1L, "Rar", "rar@mail.ru", LocalDate.parse("2000-01-01"), BigDecimal.valueOf(10000));
        Department department1 = new Department(1L, "Department 1");
        employee.setDepartment(department1);
        department1.setEmployees(List.of(employee));
        Long id = 1L, departmentId = 2L;
        String email = "hor@gmail.com";
        BigDecimal salary = BigDecimal.valueOf(15000);

        when(employeeRepository.findById(id)).thenReturn(Optional.of(employee));
        when(employeeRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(departmentService.validatedDepartmentId(departmentId)).thenThrow(new ResourceNotFoundException("Department not found by id=%s".formatted(departmentId)));

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
                employeeService.updateEmployee(id, email, salary, departmentId));

        assertEquals("Department not found by id=%s".formatted(departmentId), exception.getMessage());

        verify(employeeRepository, times(1)).findById(id);
        verify(employeeRepository, times(1)).findByEmail(email);
        verify(departmentService, times(1)).validatedDepartmentId(departmentId);
    }
}
