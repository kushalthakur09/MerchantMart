package com.main.MerchantMart.service;

import jakarta.mail.MessagingException;

import java.util.Map;

public interface EmailService {
    void sendEmail(String to, String subject, String body);
    void sendHtmlEmail(String to, String subject, String html) throws MessagingException;
    void sendTemplateEmail(String to,
                           String subject,
                           String templateName,
                           Map<String, Object> variables) throws MessagingException;

    void sendWelcomeEmail(String to, String name);
    void sendOtpEmail(
            String to,
            String name,
            String otp,
            int expiryMinutes
    ) throws MessagingException;
}
