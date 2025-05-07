package com.example.springbootguide.controllerTests;

import com.example.springbootguide.DTO.DepartmentDTO;
import com.example.springbootguide.controllers.DepartmentController;
import com.example.springbootguide.services.DepartmentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DepartmentController.class)
public class DepartmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DepartmentService departmentService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetDepartments_Success() throws Exception {
        List<DepartmentDTO> departments = List.of(new DepartmentDTO(1L, "A",10, BigDecimal.valueOf(10000)),
                new DepartmentDTO(2L, "B",5, BigDecimal.valueOf(15000)));

        when(departmentService.getDepartments(0, 5)).thenReturn(departments);

        mockMvc.perform(get("/departments")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].departmentId").value(1L))
                .andExpect(jsonPath("$[0].departmentName").value("A"))
                .andExpect(jsonPath("$[0].employeeSize").value(10))
                .andExpect(jsonPath("$[0].averageSalary").value(new BigDecimal(10000)))
                .andExpect(jsonPath("$[1].departmentId").value(2L))
                .andExpect(jsonPath("$[1].departmentName").value("B"))
                .andExpect(jsonPath("$[1].employeeSize").value(5))
                .andExpect(jsonPath("$[1].averageSalary").value(new BigDecimal(15000)));

        verify(departmentService, times(1)).getDepartments(0, 5);
    }

    @Test
    public void testCreateDepartment_Success() throws Exception {
        DepartmentDTO departmentDTO = new DepartmentDTO(null, "A",10, BigDecimal.valueOf(10000));
        DepartmentDTO savedDepartmentDTO = new DepartmentDTO(1L, "A",10, BigDecimal.valueOf(10000));

        when(departmentService.createDepartment(any(DepartmentDTO.class))).thenReturn(savedDepartmentDTO);

        mockMvc.perform(post("/departments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(departmentDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.departmentId").value(1L))
                .andExpect(jsonPath("$.departmentName").value("A"))
                .andExpect(jsonPath("$.employeeSize").value(10))
                .andExpect(jsonPath("$.averageSalary").value(new BigDecimal(10000)));

        verify(departmentService, times(1)).createDepartment(any(DepartmentDTO.class));
    }

    @Test
    public void testCreateDepartment_BlankName() throws Exception {
        DepartmentDTO departmentDTO = new DepartmentDTO(null, "",10, BigDecimal.valueOf(10000));

        mockMvc.perform(post("/departments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(departmentDTO)))
                .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.departmentName").value("Name can't be empty"));
    }

    @Test
    void testDeleteDepartment_Success() throws Exception {
        Long departmentId = 1L;

        doNothing().when(departmentService).deleteDepartment(departmentId);

        mockMvc.perform(delete("/departments/{departmentId}", departmentId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(departmentService, times(1)).deleteDepartment(departmentId);
    }

    @Test
    void testDeleteDepartment_NotPositiveOrNullId() throws Exception {
        Long departmentId = -1L;

        mockMvc.perform(delete("/departments/{departmentId}", departmentId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Id must be greater than 0 and can't be empty"));
    }

    @Test
    void testUpdateDepartment_Success() throws Exception {
        Long departmentId = 1L;
        String departmentName = "A";

        doNothing().when(departmentService).updateDepartment(departmentId, departmentName);

        mockMvc.perform(put("/departments/{departmentId}", departmentId)
                        .param("departmentName", departmentName)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(departmentService, times(1)).updateDepartment(departmentId, departmentName);
    }

    @Test
    void testUpdateDepartment_NotPositiveOrNullId() throws Exception {
        Long departmentId = -1L;
        String departmentName = "A";

        mockMvc.perform(put("/departments/{departmentId}", departmentId)
                        .param("departmentName", departmentName)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Id must be greater than 0 and can't be empty"));
    }

    @Test
    void testUpdateDepartment_BlankName() throws Exception {
        Long departmentId = 1L;
        String departmentName = "";

        mockMvc.perform(put("/departments/{departmentId}", departmentId)
                        .param("departmentName", departmentName)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Name can't be empty"));
    }
}

