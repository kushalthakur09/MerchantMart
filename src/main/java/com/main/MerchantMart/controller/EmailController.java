package com.main.MerchantMart.controller;

import com.main.MerchantMart.service.EmailService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/email")
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;

    @PostMapping("/send")
    public ResponseEntity<String> sendEmail(
            @RequestParam String to,
            @RequestParam String subject,
            @RequestParam String body) {

        emailService.sendEmail(to, subject, body);

        return ResponseEntity.ok("Email sent successfully.");
    }

    @PostMapping("/send-html")
    public ResponseEntity<String> sendHtmlEmail(@RequestParam String to) throws MessagingException {

        String html = """
                
                """;

        emailService.sendHtmlEmail(
                to,
                "MerchantMart HTML Email",
                html
        );

        return ResponseEntity.ok("HTML email sent successfully.");
    }

    @PostMapping("/send-template")
    public ResponseEntity<String> sendTemplateEmail(@RequestParam String to) throws MessagingException {

        emailService.sendTemplateEmail(
                to,
                "Welcome to MerchantMart",
                "email/welcome-email",
                Map.of(
                        "name", "Sachin"
                )
        );

        return ResponseEntity.ok("Template email sent successfully.");
    }
}