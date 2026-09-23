package com.main.MerchantMart.service.impl;

import com.main.MerchantMart.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    private final SpringTemplateEngine templateEngine;

    @Override
    public void sendEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(fromEmail);

        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);
    }

    @Override
    public void sendHtmlEmail(String to, String subject, String html) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
        helper.setFrom(fromEmail);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(html, true);
        mailSender.send(mimeMessage);
    }

    @Override
    public void sendTemplateEmail(String to,
                                  String subject,
                                  String templateName,
                                  Map<String, Object> variables) throws MessagingException {

        Context context = new Context();
        context.setVariables(variables);
        String html = templateEngine.process(templateName, context);
        sendHtmlEmail(to, subject, html);
    }

    @Override
    public void sendWelcomeEmail(String to, String name) {
        try {
            Map<String, Object> variables = new HashMap<>();
            variables.put("name", name);

            sendTemplateEmail(to, "Welcome to MerchantMart", "email/welcome-email", variables);

        } catch (MessagingException e) {
            throw new IllegalStateException("Failed to send welcome email.", e);
        }
    }

    @Override
    public void sendOtpEmail(
            String to,
            String name,
            String otp,
            int expiryMinutes
    ) throws MessagingException {

        Map<String, Object> variables = new HashMap<>();

        variables.put("name", name);
        variables.put("otp", otp);
        variables.put("expiryMinutes", expiryMinutes);

        sendTemplateEmail(to,
                "MerchantMart OTP Verification",
                "email/otp",
                variables
        );
    }
}
