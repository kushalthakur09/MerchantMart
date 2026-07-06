package com.main.MerchantMart.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.main.MerchantMart.config.OAuth2SuccessHandler;
import com.main.MerchantMart.domain.Role;
import com.main.MerchantMart.payload.response.AuthResponse;
import com.main.MerchantMart.payload.request.LoginRequest;
import com.main.MerchantMart.payload.request.SignupRequest;
import com.main.MerchantMart.entity.User;
import com.main.MerchantMart.exception.EmailAlreadyExistsException;
import com.main.MerchantMart.repository.UserRepository;
import com.main.MerchantMart.service.AuthService;
import com.main.MerchantMart.utility.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import(AuthControllerTest.TestSecurityConfig.class)
public class AuthControllerTest {
    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private UserRepository userRepository;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockitoBean
    private OAuth2SuccessHandler oAuth2SuccessHandler;



    private SignupRequest signupRequest;

    private LoginRequest loginRequest;

    private AuthResponse authResponse;

    @TestConfiguration
    static class TestSecurityConfig {
        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
            http.csrf(csrf -> csrf.disable())
                    .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
            return http.build();
        }
    }

    @BeforeEach
    public void setUp() {
        signupRequest = new SignupRequest();
        signupRequest.setEmail("test123@gmail.com");
        signupRequest.setPassword("test@123");
        signupRequest.setFullUserName("test123");
        signupRequest.setPhoneNo("9876543211");

        loginRequest = new LoginRequest();
        loginRequest.setEmail("test123@gmail.com");
        loginRequest.setPassword("test@123");

        User user=new User();
        user.setEmail("test123@gmail.com");
        user.setPassword("test@123");
        user.setRole(Role.ROLE_USER);
        user.setFullUserName("test123");
        user.setPhoneNo("9876543211");
        user.setLastLoginDate(LocalDateTime.now());

        authResponse=new AuthResponse("dummy-token","Signup Succesful",UserMapper.toDto(user));
    }

    @Test
    public void signup_shouldReturn201_whenRequestIsValid() throws Exception {

        when(authService.signup(any(SignupRequest.class))).thenReturn(authResponse);

        mockMvc.perform(post("/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(signupRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    public void signup_shouldReturn409_whenEmailAlreadyExists() throws Exception {
        when(authService.signup(any(SignupRequest.class))).thenThrow(new EmailAlreadyExistsException("Email already exists"));

        mockMvc.perform(post("/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(signupRequest)))
                .andExpect(status().isConflict());
    }

    @Test
    public void login_shouldReturn200_whenCredentialsAreValid() throws Exception {
        when(authService.login(any(LoginRequest.class))).thenReturn(authResponse);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }
}
