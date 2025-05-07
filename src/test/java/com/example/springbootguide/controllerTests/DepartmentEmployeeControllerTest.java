package com.example.springbootguide.controllerTests;

import com.example.springbootguide.DTO.DepartmentDTO;
import com.example.springbootguide.DTO.EmployeeDTO;
import com.example.springbootguide.DTO.EmployeeDepartmentDTO;
import com.example.springbootguide.controllers.DepartmentEmployeeController;
import com.example.springbootguide.services.DepartmentEmployeeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DepartmentEmployeeController.class)
public class DepartmentEmployeeControllerTest {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DepartmentEmployeeService departmentEmployeeService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    public void testGetAllEmployeesWithDepartments_Success() throws Exception {
        List<EmployeeDepartmentDTO> dtos = List.of(new EmployeeDepartmentDTO(1L, "Vasya", 1L, "A"),
                new EmployeeDepartmentDTO(2L, "Pasha", 1L, "A"),
                new EmployeeDepartmentDTO(3L, "Jim", 2L, "B"));

        when(departmentEmployeeService.getAllEmployeesWithDepartments(0, 5)).thenReturn(dtos);

        mockMvc.perform(get("/with-departments")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].employeeId").value(1L))
                .andExpect(jsonPath("$[0].employeeName").value("Vasya"))
                .andExpect(jsonPath("$[0].departmentId").value(1L))
                .andExpect(jsonPath("$[0].departmentName").value("A"))
                .andExpect(jsonPath("$[1].employeeId").value(2L))
                .andExpect(jsonPath("$[1].employeeName").value("Pasha"))
                .andExpect(jsonPath("$[1].departmentId").value(1L))
                .andExpect(jsonPath("$[1].departmentName").value("A"))
                .andExpect(jsonPath("$[2].employeeId").value(3L))
                .andExpect(jsonPath("$[2].employeeName").value("Jim"))
                .andExpect(jsonPath("$[2].departmentId").value(2L))
                .andExpect(jsonPath("$[2].departmentName").value("B"));

        verify(departmentEmployeeService, times(1)).getAllEmployeesWithDepartments(0, 5);
    }

    @Test
    public void testGetDepartmentWithMostEmployees_Success() throws Exception {
        DepartmentDTO departmentDTO = new DepartmentDTO(7L, "MOST", 111, BigDecimal.valueOf(80000));

        when(departmentEmployeeService.getDepartmentWithMostEmployees()).thenReturn(departmentDTO);

        mockMvc.perform(get("/most-employees")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.departmentId").value(7L))
                .andExpect(jsonPath("$.departmentName").value("MOST"))
                .andExpect(jsonPath("$.employeeSize").value(111))
                .andExpect(jsonPath("$.averageSalary").value(80000));


        verify(departmentEmployeeService, times(1)).getDepartmentWithMostEmployees();
    }

    @Test
    public void testGetDepartmentWithMinimalEmployees_Success() throws Exception {
        DepartmentDTO departmentDTO = new DepartmentDTO(3L, "MINIMAL", 1111, BigDecimal.valueOf(50000));

        when(departmentEmployeeService.getDepartmentWithMinimalEmployees()).thenReturn(departmentDTO);

        mockMvc.perform(get("/minimal-employees")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.departmentId").value(3L))
                .andExpect(jsonPath("$.departmentName").value("MINIMAL"))
                .andExpect(jsonPath("$.employeeSize").value(1111))
                .andExpect(jsonPath("$.averageSalary").value(50000));


        verify(departmentEmployeeService, times(1)).getDepartmentWithMinimalEmployees();
    }

    @Test
    public void testGetTop10DepartmentsByAverageSalary_Success() throws Exception {
        List<DepartmentDTO> dtos = List.of(new DepartmentDTO(3L, "MINIMAL", 1111, BigDecimal.valueOf(50000)),
                new DepartmentDTO(4L, "MINIMAL", 1111, BigDecimal.valueOf(50000)));

        when(departmentEmployeeService.getTop10DepartmentsByAverageSalary()).thenReturn(dtos);

        mockMvc.perform(get("/top10-departments-by-average-salary")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].departmentId").value(3L))
                .andExpect(jsonPath("$[0].departmentName").value("MINIMAL"))
                .andExpect(jsonPath("$[0].employeeSize").value(1111))
                .andExpect(jsonPath("$[0].averageSalary").value(50000))
                .andExpect(jsonPath("$[1].departmentId").value(4L))
                .andExpect(jsonPath("$[1].departmentName").value("MINIMAL"))
                .andExpect(jsonPath("$[1].employeeSize").value(1111))
                .andExpect(jsonPath("$[1].averageSalary").value(50000));


        verify(departmentEmployeeService, times(1)).getTop10DepartmentsByAverageSalary();
    }

    @Test
    public void testGetEmployeesBySalaryGreaterThan_Success() throws Exception {
        List<EmployeeDTO> dtos = List.of(new EmployeeDTO(1L, "Vasya", "vasya@gmail.com", LocalDate.parse("2000-01-03"), BigDecimal.valueOf(50000), 1L),
                new EmployeeDTO(2L, "Jim", "jim@mail.ru", LocalDate.parse("2000-03-01"), BigDecimal.valueOf(45000), 2L));
        BigDecimal bigDecimal = BigDecimal.valueOf(40000);

        when(departmentEmployeeService.getEmployeesBySalaryGreaterThan(bigDecimal, 0, 5)).thenReturn(dtos);

        mockMvc.perform(get("/salary-greater-than/{salary}", bigDecimal)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Vasya"))
                .andExpect(jsonPath("$[0].email").value("vasya@gmail.com"))
                .andExpect(jsonPath("$[0].birthDate").value("2000-01-03"))
                .andExpect(jsonPath("$[0].salary").value(BigDecimal.valueOf(50000)))
                .andExpect(jsonPath("$[0].departmentId").value(1L))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].name").value("Jim"))
                .andExpect(jsonPath("$[1].email").value("jim@mail.ru"))
                .andExpect(jsonPath("$[1].birthDate").value("2000-03-01"))
                .andExpect(jsonPath("$[1].salary").value(BigDecimal.valueOf(45000)))
                .andExpect(jsonPath("$[1].departmentId").value(2L));


        verify(departmentEmployeeService, times(1)).getEmployeesBySalaryGreaterThan(bigDecimal, 0, 5);
    }
}
