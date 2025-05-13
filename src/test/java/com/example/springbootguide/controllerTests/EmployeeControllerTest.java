package com.example.springbootguide.controllerTests;

import com.example.springbootguide.DTO.EmployeeDTO;
import com.example.springbootguide.services.EmployeeService;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


public class EmployeeControllerTest extends BaseControllerTest {

    @MockitoBean
    private EmployeeService employeeService;

    @Test
    public void testGetEmployee_Success() throws Exception {
        List<EmployeeDTO> employees = List.of(new EmployeeDTO(1L, "Vasya", "vasya@gmail.com", LocalDate.parse("2000-01-03"), BigDecimal.valueOf(50000), 1L),
                new EmployeeDTO(2L, "Jim", "jim@mail.ru", LocalDate.parse("2000-03-01"), BigDecimal.valueOf(45000), 2L));

        when(employeeService.getEmployees(0, 5)).thenReturn(employees);

        mockMvc.perform(get("/employees")
                        .header("Authorization", "Bearer " + getAccessUserToken())
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
    public void testCreateEmployee_Success() throws Exception {
        EmployeeDTO employeeDTO = new EmployeeDTO(null, "Vasya", "vasya@gmail.com", LocalDate.parse("2000-01-03"), BigDecimal.valueOf(50000), 1L);
        EmployeeDTO savedEmployeeDTO = new EmployeeDTO(1L, "Vasya", "vasya@gmail.com", LocalDate.parse("2000-01-03"), BigDecimal.valueOf(50000), 1L);

        when(employeeService.createEmployee(any(EmployeeDTO.class))).thenReturn(savedEmployeeDTO);

        mockMvc.perform(post("/employees")
                        .header("Authorization", "Bearer " + getAccessAdminToken())
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
    public void testCreateEmployee_BlankName() throws Exception {
        EmployeeDTO employeeDTO = new EmployeeDTO(null, "", "vasya@gmail.com", LocalDate.parse("2000-01-03"), BigDecimal.valueOf(50000), 1L);

        mockMvc.perform(post("/employees")
                        .header("Authorization", "Bearer " + getAccessAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employeeDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").value("Name can't be empty"));
    }

    @Test
    public void testCreateEmployee_NotPatternEmail() throws Exception {
        EmployeeDTO employeeDTO = new EmployeeDTO(null, "Vasya", "vasyagmail.com", LocalDate.parse("2000-01-03"), BigDecimal.valueOf(50000), 1L);

        mockMvc.perform(post("/employees")
                        .header("Authorization", "Bearer " + getAccessAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employeeDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.email").value("Email doesn't match the form"));
    }

    @Test
    public void testCreateEmployee_NotValidBirthDate() throws Exception {
        EmployeeDTO employeeDTO = new EmployeeDTO(null, "Vasya", "vasya@gmail.com", LocalDate.parse("2020-01-03"), BigDecimal.valueOf(50000), 1L);

        mockMvc.perform(post("/employees")
                        .header("Authorization", "Bearer " + getAccessAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employeeDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.birthDate").value("Age must be at least 18 years"));
    }

    @Test
    public void testCreateEmployee_DecimalMin() throws Exception {
        EmployeeDTO employeeDTO = new EmployeeDTO(null, "Vasya", "vasya@gmail.com", LocalDate.parse("2000-01-03"), BigDecimal.valueOf(4999), 1L);

        mockMvc.perform(post("/employees")
                        .header("Authorization", "Bearer " + getAccessAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employeeDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.salary").value("Salary must be bigger than 5000"));
    }

    @Test
    public void testCreateEmployee_NotPositiveDepartmentId() throws Exception {
        EmployeeDTO employeeDTO = new EmployeeDTO(null, "Vasya", "vasya@gmail.com", LocalDate.parse("2000-01-03"), BigDecimal.valueOf(49999), -1L);

        mockMvc.perform(post("/employees")
                        .header("Authorization", "Bearer " + getAccessAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employeeDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.departmentId").value("Id must be greater than 0"));
    }

    @Test
    void testDeleteEmployee_Success() throws Exception {
        Long id = 1L;

        doNothing().when(employeeService).deleteEmployee(id);

        mockMvc.perform(delete("/employees/{employeeId}", id)
                        .header("Authorization", "Bearer " + getAccessAdminToken())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(employeeService, times(1)).deleteEmployee(id);
    }

    @Test
    void testDeleteEmployee_NotPositiveOrNullId() throws Exception {
        Long id = -1L;

        mockMvc.perform(delete("/employees/{employeeId}", id)
                        .header("Authorization", "Bearer " + getAccessAdminToken())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Id must be greater than 0 and can't be empty"));
    }

    @Test
    void testUpdateEmployee_Success() throws Exception {
        Long id = 1L;
        String email = "vasya25@gmail.com";
        BigDecimal salary = BigDecimal.valueOf(75000);
        Long departmentId = 2L;

        doNothing().when(employeeService).updateEmployee(id, email, salary, departmentId);

        mockMvc.perform(put("/employees/{employeeId}", id)
                        .header("Authorization", "Bearer " + getAccessAdminToken())
                        .param("email", email)
                        .param("salary", salary.toString())
                        .param("departmentId", departmentId.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(employeeService, times(1)).updateEmployee(id, email, salary, departmentId);
    }

    @Test
    void testUpdateEmployee_NotPositiveOrNullId() throws Exception {
        Long id = -1L;

        mockMvc.perform(put("/employees/{employeeId}", id)
                        .header("Authorization", "Bearer " + getAccessAdminToken())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Id must be greater than 0 and can't be empty"));
    }

    @Test
    void testUpdateEmployee_NotPatternEmail() throws Exception {
        Long id = 1L;
        String email = "vasya25gmail.com";

        mockMvc.perform(put("/employees/{employeeId}", id)
                        .header("Authorization", "Bearer " + getAccessAdminToken())
                        .param("email", email)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Email doesn't match the form"));
    }

    @Test
    void testUpdateEmployee_DecimalMin() throws Exception {
        Long id = 1L;
        BigDecimal salary = BigDecimal.valueOf(750);

        mockMvc.perform(put("/employees/{employeeId}", id)
                        .header("Authorization", "Bearer " + getAccessAdminToken())
                        .param("salary", salary.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Salary must be bigger than 5000"));
    }

    @Test
    void testUpdateEmployee_NotPositiveDepartmentId() throws Exception {
        Long id = 1L;
        Long departmentId = -2L;

        mockMvc.perform(put("/employees/{employeeId}", id)
                        .header("Authorization", "Bearer " + getAccessAdminToken())
                        .param("departmentId", departmentId.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Department id must be greater than 0"));
    }
}
