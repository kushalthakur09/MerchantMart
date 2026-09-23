package com.main.MerchantMart.controller;

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
    public ResponseEntity<Void> sendOtp(
            @RequestParam String email,
            @RequestParam String name
    ) {
        otpService.generateAndSendOtp(email, name);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/otp/verify")
    public ResponseEntity<Void> verifyOtp(
            @RequestParam String email,
            @RequestParam String otp
    ) {
        otpService.verifyOtp(email, otp);
        return ResponseEntity.ok().build();
    }
}
