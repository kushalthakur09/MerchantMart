package com.main.MerchantMart.service;

import com.main.MerchantMart.payload.response.AuthResponse;
import com.main.MerchantMart.payload.request.LoginRequest;
import com.main.MerchantMart.payload.request.SignupRequest;

public interface AuthService {
    AuthResponse signup(SignupRequest request);
    AuthResponse adminSignup(SignupRequest request);
    AuthResponse login(LoginRequest request);
    AuthResponse adminLogin(LoginRequest request);
}
