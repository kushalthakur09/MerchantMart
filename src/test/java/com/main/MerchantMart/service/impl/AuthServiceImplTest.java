package com.main.MerchantMart.service.impl;

import com.main.MerchantMart.config.JwtProvider;
import com.main.MerchantMart.payload.response.AuthResponse;
import com.main.MerchantMart.payload.request.LoginRequest;
import com.main.MerchantMart.payload.request.SignupRequest;
import com.main.MerchantMart.entity.User;
import com.main.MerchantMart.exception.EmailAlreadyExistsException;
import com.main.MerchantMart.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtProvider jwtProvider;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthServiceImpl authService;


    private SignupRequest signupRequest;

    private LoginRequest loginRequest;
    @BeforeEach
    public void setUp() {
        signupRequest = new SignupRequest();
        signupRequest.setEmail("test123@gmail.com");
        signupRequest.setPassword("test@123");
        signupRequest.setFullUserName("test123");
        signupRequest.setPhoneNo("9876543211");

        loginRequest=new LoginRequest();
        loginRequest.setEmail("test123@gmail.com");
        loginRequest.setPassword("test@123");

    }

    @Test
    public void signup_shouldCreateUser_whenEmailIsUnique() {
        when(userRepository.existsByEmail(signupRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(signupRequest.getPassword())).thenReturn("encodedPassword");
        when(jwtProvider.generateToken(any(Authentication.class))).thenReturn("dummy-jwt");

        AuthResponse authResponse = authService.signup(signupRequest);

        assertNotNull(authResponse);
        assertEquals("Signup Successful", authResponse.getMessage());
        assertEquals("dummy-jwt", authResponse.getToken());

        verify(userRepository).save(any(User.class));
    }

    @Test
    public void signup_shouldThrow_whenEmailAlreadyExists(){
        when(userRepository.existsByEmail(signupRequest.getEmail())).thenReturn(true);

        assertThrows(EmailAlreadyExistsException.class,()-> authService.signup(signupRequest));

        verify(userRepository,never()).save(any(User.class));
    }

    @Test
    public void login_shouldReturnToken_whenCredentialsAreValid(){
        Authentication authentication=new UsernamePasswordAuthenticationToken(loginRequest.getEmail(),loginRequest.getPassword());

        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(jwtProvider.generateToken(authentication)).thenReturn("dummy-jwt");
        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(new User()));

        AuthResponse authResponse=authService.login(loginRequest);
        assertNotNull(authResponse);
        assertEquals("dummy-jwt",authResponse.getToken());
        assertEquals("Login Successful",authResponse.getMessage());

        verify(userRepository).save(any(User.class));
    }
}
