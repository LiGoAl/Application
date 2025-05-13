package com.example.springbootguide.controllerTests;

import com.example.springbootguide.DTO.JwtAuthenticationDTO;
import com.example.springbootguide.DTO.RefreshTokenDTO;
import com.example.springbootguide.DTO.UserCredentialsDTO;
import com.example.springbootguide.security.jwt.JwtService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AuthControllerTest extends BaseControllerTest {

    @Autowired
    private JwtService jwtService;

    @Test
    public void testSignIn_Success() throws Exception {
        UserCredentialsDTO userCredentialsDTO = new UserCredentialsDTO();
        userCredentialsDTO.setUsername("admin");
        userCredentialsDTO.setPassword("admin_JTFTghfw874");

        String loginJSON = objectMapper.writeValueAsString(userCredentialsDTO);

        String tokens = mockMvc.perform(post("/auth/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JwtAuthenticationDTO jwtAuthenticationDTO = objectMapper.readValue(tokens, JwtAuthenticationDTO.class);

        Assertions.assertEquals(userCredentialsDTO.getUsername(), jwtService.getUsernameFromToken(jwtAuthenticationDTO.getToken()));
    }

    @Test
    public void testSignIn_Unauthorized() throws Exception {
        UserCredentialsDTO userCredentialsDTO = new UserCredentialsDTO();
        userCredentialsDTO.setUsername("admin");
        userCredentialsDTO.setPassword("admin");

        String loginJSON = objectMapper.writeValueAsString(userCredentialsDTO);

        mockMvc.perform(post("/auth/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testRefresh_Success() throws Exception {
        UserCredentialsDTO userCredentialsDTO = new UserCredentialsDTO();
        userCredentialsDTO.setUsername("admin");
        userCredentialsDTO.setPassword("admin_JTFTghfw874");

        String loginJSON = objectMapper.writeValueAsString(userCredentialsDTO);

        String tokens = mockMvc.perform(post("/auth/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        RefreshTokenDTO refreshTokenDTO = new RefreshTokenDTO();
        refreshTokenDTO.setRefreshToken(objectMapper.readValue(tokens, RefreshTokenDTO.class).getRefreshToken());
        String refreshToken = objectMapper.writeValueAsString(refreshTokenDTO);

        mockMvc.perform(post("/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(refreshToken))
                .andExpect(status().isOk());
    }
}
