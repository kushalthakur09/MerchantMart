package com.main.MerchantMart.controller;

import com.main.MerchantMart.payload.dto.SendOtpRequest;
import com.main.MerchantMart.payload.dto.VerifyOtpRequest;
import com.main.MerchantMart.payload.request.ForgotPasswordRequest;
import com.main.MerchantMart.payload.request.ResetPasswordRequest;
import com.main.MerchantMart.payload.response.AuthResponse;
import com.main.MerchantMart.payload.request.LoginRequest;
import com.main.MerchantMart.payload.request.SignupRequest;
import com.main.MerchantMart.service.AuthService;
import com.main.MerchantMart.service.OtpService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private  final AuthService authService;
    private final OtpService otpService;

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signup(@Valid @RequestBody SignupRequest request){
        return  ResponseEntity.status(HttpStatus.CREATED).body(authService.signup(request));
    }
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/admin/login")
    public ResponseEntity<AuthResponse> adminLogin(
            @Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.adminLogin(request));
    }

    @PostMapping("/otp/send")
    public ResponseEntity<Void> sendOtp(@Valid @RequestBody SendOtpRequest request
    ) {
        otpService.generateAndSendOtp(
                request.getEmail(),
                request.getName()
        );

        return ResponseEntity.ok().build();
    }

    @PostMapping("/otp/verify")
    public ResponseEntity<Void> verifyOtp(
            @Valid @RequestBody VerifyOtpRequest request
    ) {
        otpService.verifyOtp(
                request.getEmail(),
                request.getOtp()
        );

        return ResponseEntity.ok().build();
    }

    @PostMapping("/otp/resend")
    public ResponseEntity<Void> resendOtp(
            @Valid @RequestBody SendOtpRequest request
    ) {
        otpService.resendOtp(
                request.getEmail(),
                request.getName()
        );

        return ResponseEntity.ok().build();
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Void> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        authService.forgotPassword(request.getEmail());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(
                request.getEmail(),
                request.getOtp(),
                request.getNewPassword()
        );
        return ResponseEntity.ok().build();
    }
}
