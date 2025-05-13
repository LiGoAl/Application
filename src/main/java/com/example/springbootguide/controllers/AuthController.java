package com.example.springbootguide.controllers;

import com.example.springbootguide.DTO.JwtAuthenticationDTO;
import com.example.springbootguide.DTO.RefreshTokenDTO;
import com.example.springbootguide.DTO.UserCredentialsDTO;
import com.example.springbootguide.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final UserService userService;

    @PostMapping("/sign-in")
    public ResponseEntity<JwtAuthenticationDTO> signIn(@RequestBody UserCredentialsDTO userCredentialsDTO) {
        JwtAuthenticationDTO jwtAuthenticationDTO = userService.signIn(userCredentialsDTO);
        return new ResponseEntity<>(jwtAuthenticationDTO, HttpStatus.OK);
    }

    @PostMapping("/refresh")
    public JwtAuthenticationDTO refresh(@RequestBody RefreshTokenDTO refreshTokenDTO) {
        return userService.refreshToken(refreshTokenDTO);
    }
}
