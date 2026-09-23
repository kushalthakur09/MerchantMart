package com.main.MerchantMart.service;

public interface NotificationService {
    void sendWelcomeEmail(String to, String name);
    void sendOtpEmail(
            String to,
            String name,
            String otp,
            int expiryMinutes
    );
}