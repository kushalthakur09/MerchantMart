package com.main.MerchantMart.service;

public interface OtpService {

    void generateAndSendOtp(String email, String name);

    void verifyOtp(String email, String otp);
}