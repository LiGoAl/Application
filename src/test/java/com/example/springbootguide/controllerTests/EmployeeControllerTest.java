package com.example.springbootguide.controllerTests;

import com.example.springbootguide.DTO.EmployeeDTO;
import com.example.springbootguide.controllers.EmployeeController;
import com.example.springbootguide.services.EmployeeService;
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

@WebMvcTest(EmployeeController.class)
public class EmployeeControllerTest {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EmployeeService employeeService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    public void testGetDepartments_Success() throws Exception {
        List<EmployeeDTO> employees = List.of(new EmployeeDTO(1L, "Vasya", "vasya@gmail.com", LocalDate.parse("2000-01-03"), BigDecimal.valueOf(50000), 1L),
                new EmployeeDTO(2L, "Jim", "jim@mail.ru", LocalDate.parse("2000-03-01"), BigDecimal.valueOf(45000), 2L));

        when(employeeService.getEmployees(0, 5)).thenReturn(employees);

        mockMvc.perform(get("/employees")
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

        verify(employeeService, times(1)).getEmployees(0, 5);
    }

    @Test
    public void testCreateDepartment_Success() throws Exception {
        EmployeeDTO employeeDTO = new EmployeeDTO(null, "Vasya", "vasya@gmail.com", LocalDate.parse("2000-01-03"), BigDecimal.valueOf(50000), 1L);
        EmployeeDTO savedEmployeeDTO = new EmployeeDTO(1L, "Vasya", "vasya@gmail.com", LocalDate.parse("2000-01-03"), BigDecimal.valueOf(50000), 1L);

        when(employeeService.createEmployee(any(EmployeeDTO.class))).thenReturn(savedEmployeeDTO);

        mockMvc.perform(post("/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employeeDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Vasya"))
                .andExpect(jsonPath("$.email").value("vasya@gmail.com"))
                .andExpect(jsonPath("$.birthDate").value("2000-01-03"))
                .andExpect(jsonPath("$.salary").value(BigDecimal.valueOf(50000)))
                .andExpect(jsonPath("$.departmentId").value(1L));

        verify(employeeService, times(1)).createEmployee(any(EmployeeDTO.class));
    }

    @Test
    public void testCreateDepartment_BlankName() throws Exception {
        EmployeeDTO employeeDTO = new EmployeeDTO(null, "", "vasya@gmail.com", LocalDate.parse("2000-01-03"), BigDecimal.valueOf(50000), 1L);

        mockMvc.perform(post("/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employeeDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").value("Name can't be empty"));
    }

    @Test
    public void testCreateDepartment_NotPatternEmail() throws Exception {
        EmployeeDTO employeeDTO = new EmployeeDTO(null, "Vasya", "vasyagmail.com", LocalDate.parse("2000-01-03"), BigDecimal.valueOf(50000), 1L);

        mockMvc.perform(post("/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employeeDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.email").value("Email doesn't match the form"));
    }

    @Test
    public void testCreateDepartment_NotValidBirthDate() throws Exception {
        EmployeeDTO employeeDTO = new EmployeeDTO(null, "Vasya", "vasya@gmail.com", LocalDate.parse("2020-01-03"), BigDecimal.valueOf(50000), 1L);

        mockMvc.perform(post("/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employeeDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.birthDate").value("Age must be at least 18 years"));
    }

    @Test
    public void testCreateDepartment_DecimalMin() throws Exception {
        EmployeeDTO employeeDTO = new EmployeeDTO(null, "Vasya", "vasya@gmail.com", LocalDate.parse("2000-01-03"), BigDecimal.valueOf(4999), 1L);

        mockMvc.perform(post("/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employeeDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.salary").value("Salary must be bigger than 5000"));
    }

    @Test
    public void testCreateDepartment_NotPositiveDepartmentId() throws Exception {
        EmployeeDTO employeeDTO = new EmployeeDTO(null, "Vasya", "vasya@gmail.com", LocalDate.parse("2000-01-03"), BigDecimal.valueOf(49999), -1L);

        mockMvc.perform(post("/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employeeDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.departmentId").value("Id must be greater than 0"));
    }

    @Test
    void testDeleteDepartment_Success() throws Exception {
        Long id = 1L;

        doNothing().when(employeeService).deleteEmployee(id);

        mockMvc.perform(delete("/employees/{employeeId}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(employeeService, times(1)).deleteEmployee(id);
    }

    @Test
    void testDeleteDepartment_NotPositiveOrNullId() throws Exception {
        Long id = -1L;

        mockMvc.perform(delete("/employees/{employeeId}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Id must be greater than 0 and can't be empty"));
    }

    @Test
    void testUpdateDepartment_Success() throws Exception {
        Long id = 1L;
        String email = "vasya25@gmail.com";
        BigDecimal salary = BigDecimal.valueOf(75000);
        Long departmentId = 2L;

        doNothing().when(employeeService).updateEmployee(id, email, salary, departmentId);

        mockMvc.perform(put("/employees/{employeeId}", id)
                        .param("email", email)
                        .param("salary", salary.toString())
                        .param("departmentId", departmentId.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(employeeService, times(1)).updateEmployee(id, email, salary, departmentId);
    }

    @Test
    void testUpdateDepartment_NotPositiveOrNullId() throws Exception {
        Long id = -1L;

        mockMvc.perform(put("/employees/{employeeId}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Id must be greater than 0 and can't be empty"));
    }

    @Test
    void testUpdateDepartment_NotPatternEmail() throws Exception {
        Long id = 1L;
        String email = "vasya25gmail.com";

        mockMvc.perform(put("/employees/{employeeId}", id)
                        .param("email", email)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Email doesn't match the form"));
    }

    @Test
    void testUpdateDepartment_DecimalMin() throws Exception {
        Long id = 1L;
        BigDecimal salary = BigDecimal.valueOf(750);

        mockMvc.perform(put("/employees/{employeeId}", id)
                        .param("salary", salary.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Salary must be bigger than 5000"));
    }

    @Test
    void testUpdateDepartment_NotPositiveDepartmentId() throws Exception {
        Long id = 1L;
        Long departmentId = -2L;

        mockMvc.perform(put("/employees/{employeeId}", id)
                        .param("departmentId", departmentId.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Department id must be greater than 0"));
    }
}
