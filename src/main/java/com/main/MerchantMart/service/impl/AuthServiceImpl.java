package com.main.MerchantMart.service.impl;

import com.main.MerchantMart.config.JwtProvider;
import com.main.MerchantMart.domain.Role;
import com.main.MerchantMart.utility.contants.ExceptionMessageConstants;
import com.main.MerchantMart.payload.response.AuthResponse;
import com.main.MerchantMart.payload.request.LoginRequest;
import com.main.MerchantMart.payload.request.SignupRequest;
import com.main.MerchantMart.entity.User;
import com.main.MerchantMart.exception.EmailAlreadyExistsException;
import com.main.MerchantMart.repository.UserRepository;
import com.main.MerchantMart.service.AuthService;
import com.main.MerchantMart.utility.contants.AuthConstants;
import com.main.MerchantMart.utility.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Override
    public AuthResponse signup(SignupRequest request) {
            User user=createUser(request);
            return buildAuthResponse(user,AuthConstants.SIGNUP_SUCCESS);
    }

    @Override
    public AuthResponse adminSignup(SignupRequest request){
//        User user=createUser(request,Role.ROLE_ADMIN);
        User user=createUser(request);
        return buildAuthResponse(user,AuthConstants.SIGNUP_SUCCESS);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        return authenticateUser(request,ExceptionMessageConstants.USE_ADMIN_LOGIN);
    }

    @Override
    public AuthResponse adminLogin(LoginRequest request) {
        return authenticateUser( request,ExceptionMessageConstants.USE_USER_LOGIN);
    }

    private  User createUser(SignupRequest request){
            if(userRepository.existsByEmail(request.getEmail())){
                throw new EmailAlreadyExistsException(ExceptionMessageConstants.EMAIL_ALREADY_EXITS);
            }

            if(Role.ROLE_ADMIN.equals(request.getRole())){
                throw new AccessDeniedException("Role Admin Is Not Allowed");
            }
            User user=new User();
            user.setFullUserName(request.getFullUserName());
            user.setEmail(request.getEmail());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setPhoneNo(request.getPhoneNo());
            user.setRole(request.getRole());
            user.setLastLoginDate(LocalDateTime.now());

            return userRepository.save(user);
    }

    private AuthResponse buildAuthResponse(
            User user,
            String message) {

        GrantedAuthority authority =
                new SimpleGrantedAuthority(user.getRole().name());

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        user.getEmail(),
                        null,
                        Collections.singleton(authority));

        String token = jwtProvider.generateToken(authentication);

        return new AuthResponse(
                token,
                message,
                UserMapper.toDto(user));
    }

    private AuthResponse authenticateUser(
            LoginRequest request,
            String errorMessage) {

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.getEmail(),
                                request.getPassword()));

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                ExceptionMessageConstants.USER_NO_FOUND));

        if(Role.ROLE_ADMIN.equals(user.getRole())){
            throw new AccessDeniedException("Role Admin Is Not Allowed");
        }

        user.setLastLoginDate(LocalDateTime.now());
        userRepository.save(user);

        String token = jwtProvider.generateToken(authentication);

        return new AuthResponse(
                token,
                AuthConstants.LOGIN_SUCCESS,
                UserMapper.toDto(user));
    }

}
