package com.example.springbootguide.serviceTests;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.springbootguide.DTO.DepartmentDTO;
import com.example.springbootguide.exception.ResourceAlreadyOccupiedException;
import com.example.springbootguide.exception.ResourceNotFoundException;
import com.example.springbootguide.model.Department;
import com.example.springbootguide.model.Employee;
import com.example.springbootguide.repository.DepartmentRepository;
import com.example.springbootguide.service.DepartmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class DepartmentServiceTest {

    @Mock
    private DepartmentRepository departmentRepository;

    @InjectMocks
    private DepartmentService departmentService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetDepartments_Success() {
        Employee employee1 = new Employee(1L, "Jar", "jar@mail.ru", LocalDate.parse("2000-01-01"), BigDecimal.valueOf(10000));
        Employee employee2 = new Employee(2L, "Rar", "rar@mail.ru", LocalDate.parse("2002-01-01"), BigDecimal.valueOf(20000));
        Department department1 = new Department(1L, "Department 1");
        Department department2 = new Department(2L, "Department 2");
        department1.setEmployees(List.of(employee1));
        department2.setEmployees(List.of(employee2));
        employee1.setDepartment(department1);
        employee2.setDepartment(department2);

        when(departmentRepository.findAll()).thenReturn(List.of(department1, department2));

        List<DepartmentDTO> result = departmentService.getDepartments();

        assertEquals(2, result.size());

        DepartmentDTO dto1 = result.get(0);
        assertEquals(1L, dto1.getDepartmentId());
        assertEquals("Department 1", dto1.getDepartmentName());
        assertEquals(1, dto1.getEmployeeSize());
        assertEquals(0, dto1.getAverageSalary().compareTo(BigDecimal.valueOf(10000)));

        DepartmentDTO dto2 = result.get(1);
        assertEquals(2L, dto2.getDepartmentId());
        assertEquals("Department 2", dto2.getDepartmentName());
        assertEquals(1, dto2.getEmployeeSize());
        assertEquals(0, dto2.getAverageSalary().compareTo(BigDecimal.valueOf(20000)));

        verify(departmentRepository, times(1)).findAll();
    }

    @Test
    public void testCreateDepartment_Success() {
        DepartmentDTO departmentDTO = new DepartmentDTO(null, "New Department", 0, BigDecimal.ZERO);
        Department department = new Department(1L, "New Department");

        when(departmentRepository.findByName("New Department")).thenReturn(Optional.empty());
        when(departmentRepository.save(any(Department.class))).thenReturn(department);

        DepartmentDTO result = departmentService.createDepartment(departmentDTO);

        assertNotNull(result);
        assertEquals(1L, result.getDepartmentId());
        assertEquals("New Department", result.getDepartmentName());
        assertEquals(0, result.getEmployeeSize());
        assertEquals(0, result.getAverageSalary().compareTo(BigDecimal.ZERO));

        verify(departmentRepository, times(1)).findByName("New Department");
        verify(departmentRepository, times(1)).save(any(Department.class));
    }

    @Test
    public void testCreateDepartment_IllegalId() {
        DepartmentDTO departmentDTO = new DepartmentDTO(1L, "Some Department", 0, BigDecimal.ZERO);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            departmentService.createDepartment(departmentDTO));

        assertEquals("Id must be empty", exception.getMessage());
    }

    @Test
    public void testCreateDepartment_NameAlreadyTaken() {
        DepartmentDTO departmentDTO = new DepartmentDTO(null, "Taken Department", 0, BigDecimal.ZERO);
        Department existingDepartment = new Department(1L, "Taken Department");

        when(departmentRepository.findByName(existingDepartment.getName())).thenReturn(Optional.of(existingDepartment));

        ResourceAlreadyOccupiedException exception = assertThrows(ResourceAlreadyOccupiedException.class, () ->
            departmentService.createDepartment(departmentDTO));


        assertEquals("Name already taken", exception.getMessage());

        verify(departmentRepository, times(1)).findByName(existingDepartment.getName());
    }

    @Test
    public void testDeleteDepartment_Success() {
        Department department = new Department(1L, "Department 1");
        Long departmentId = 1L;

        when(departmentRepository.findById(departmentId)).thenReturn(Optional.of(department));
        doNothing().when(departmentRepository).deleteById(departmentId);

        departmentService.deleteDepartment(departmentId);

        verify(departmentRepository, times(1)).findById(departmentId);
        verify(departmentRepository, times(1)).deleteById(departmentId);
    }

    @Test
    public void testDeleteDepartment_NotFound() {
        Long departmentId = 1L;

        when(departmentRepository.findById(departmentId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
            departmentService.deleteDepartment(departmentId));

        assertEquals("Department not found by id=%s".formatted(departmentId), exception.getMessage());

        verify(departmentRepository, times(1)).findById(departmentId);
    }

    @Test
    public void testUpdateDepartment_Success() {
        Department department = new Department(1L, "Department 1");
        Long departmentId = 1L;
        String departmentName = "New Name";

        when(departmentRepository.findByName(departmentName)).thenReturn(Optional.empty());
        when(departmentRepository.findById(departmentId)).thenReturn(Optional.of(department));

        departmentService.updateDepartment(departmentId, departmentName);

        assertEquals(departmentName, department.getName());

        verify(departmentRepository, times(1)).findByName(departmentName);
        verify(departmentRepository, times(2)).findById(departmentId);
    }

    @Test
    public void testUpdateDepartment_NameAlreadyTaken() {
        Department existingDepartment = new Department(1L, "Department 1");
        Long departmentId = 1L;
        String departmentName = "Department 1";

        when(departmentRepository.findByName(departmentName)).thenReturn(Optional.of(existingDepartment));

        ResourceAlreadyOccupiedException exception = assertThrows(ResourceAlreadyOccupiedException.class, () ->
                departmentService.updateDepartment(departmentId, departmentName));

        assertEquals("Name already taken", exception.getMessage());

        verify(departmentRepository, times(1)).findByName(departmentName);
    }

    @Test
    public void testUpdateDepartment_NotFound() {
        Long departmentId = 2L;
        String departmentName = "Department 2";

        when(departmentRepository.findByName(departmentName)).thenReturn(Optional.empty());
        when(departmentRepository.findById(departmentId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
                departmentService.updateDepartment(departmentId, departmentName));

        assertEquals("Department not found by id=%s".formatted(departmentId), exception.getMessage());

        verify(departmentRepository, times(1)).findByName(departmentName);
        verify(departmentRepository, times(1)).findById(departmentId);
    }
}