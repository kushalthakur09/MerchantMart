package com.main.MerchantMart.service.impl;

import com.main.MerchantMart.service.EmailService;
import com.main.MerchantMart.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final EmailService emailService;

    @Override
    public void sendWelcomeEmail(String to, String name) {
        try {
            emailService.sendWelcomeEmail(to, name);
        } catch (Exception e) {
            log.error("Failed to send welcome email to {}", to, e);
        }
    }

    @Override
    public void sendOtpEmail(
            String to,
            String name,
            String otp,
            int expiryMinutes
    ) {
        try {
            emailService.sendOtpEmail(
                    to,
                    name,
                    otp,
                    expiryMinutes
            );
        } catch (Exception e) {
            log.error(
                    "Failed to send OTP email to {}",
                    to,
                    e
            );
        }
    }
}