package com.example.springbootguide.services;

import com.example.springbootguide.DTO.JwtAuthenticationDTO;
import com.example.springbootguide.DTO.RefreshTokenDTO;
import com.example.springbootguide.DTO.UserCredentialsDTO;
import com.example.springbootguide.exceptions.AuthenticationException;
import com.example.springbootguide.models.User;
import com.example.springbootguide.repositories.UserRepository;
import com.example.springbootguide.security.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public JwtAuthenticationDTO signIn(UserCredentialsDTO userCredentialsDTO) {
        User user = findByCredentials(userCredentialsDTO);
        return jwtService.generateAuthToken(user.getUsername(), user.getRoles());
    }

    public JwtAuthenticationDTO refreshToken(RefreshTokenDTO refreshTokenDTO) {
        String refreshToken = refreshTokenDTO.getRefreshToken();
        if (refreshToken != null && jwtService.validateJwtToken(refreshToken)) {
            User user = findByUsername(jwtService.getUsernameFromToken(refreshToken));
            return jwtService.refreshBaseToken(user.getUsername(), refreshToken, user.getRoles());
        }
        throw new AuthenticationException("Invalid refresh token");
    }

    private User findByCredentials(UserCredentialsDTO userCredentialsDTO) {
        Optional<User> optionalUser = userRepository.findByUsername(userCredentialsDTO.getUsername());
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (passwordEncoder.matches(userCredentialsDTO.getPassword(), user.getPassword())) {
                return user;
            }
        }
        throw new AuthenticationException("Username or password is not correct");
    }

    private User findByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("Username not found"));
    }
}
