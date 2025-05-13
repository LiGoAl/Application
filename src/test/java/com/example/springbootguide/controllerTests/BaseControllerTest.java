package com.example.springbootguide.controllerTests;

import com.example.springbootguide.DTO.JwtAuthenticationDTO;
import com.example.springbootguide.DTO.UserCredentialsDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public abstract class BaseControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        objectMapper.registerModule(new JavaTimeModule());
    }

    protected String getAccessUserToken() throws Exception {
        UserCredentialsDTO userCredentialsDTO = new UserCredentialsDTO();
        userCredentialsDTO.setPassword("user_Ouhrtgeo85");
        userCredentialsDTO.setUsername("user");

        String loginJSON = objectMapper.writeValueAsString(userCredentialsDTO);

        String tokens = mockMvc.perform(post("/auth/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJSON))
                .andReturn()
                .getResponse()
                .getContentAsString();

        JwtAuthenticationDTO jwtAuthenticationDTO = objectMapper.readValue(tokens, JwtAuthenticationDTO.class);

        return jwtAuthenticationDTO.getToken();
    }

    protected String getAccessAdminToken() throws Exception {
        UserCredentialsDTO userCredentialsDTO = new UserCredentialsDTO();
        userCredentialsDTO.setPassword("admin_JTFTghfw874");
        userCredentialsDTO.setUsername("admin");

        String loginJSON = objectMapper.writeValueAsString(userCredentialsDTO);

        String tokens = mockMvc.perform(post("/auth/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJSON))
                .andReturn()
                .getResponse()
                .getContentAsString();

        JwtAuthenticationDTO jwtAuthenticationDTO = objectMapper.readValue(tokens, JwtAuthenticationDTO.class);

        return jwtAuthenticationDTO.getToken();
    }
}
