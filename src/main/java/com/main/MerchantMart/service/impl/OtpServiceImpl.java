package com.main.MerchantMart.service.impl;

import com.main.MerchantMart.entity.OtpVerification;
import com.main.MerchantMart.entity.User;
import com.main.MerchantMart.repository.OtpVerificationRepository;
import com.main.MerchantMart.repository.UserRepository;
import com.main.MerchantMart.service.NotificationService;
import com.main.MerchantMart.service.OtpService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class OtpServiceImpl implements OtpService {

    private static final int OTP_EXPIRY_MINUTES = 5;
    private static final int RESEND_COOLDOWN_SECONDS = 60;

    private final OtpVerificationRepository otpVerificationRepository;
    private final NotificationService notificationService;
    private final UserRepository userRepository;
    @Value("${app.otp.dev-mode:false}")
    private boolean otpDevMode;

    @Override
    @Transactional
    public void generateAndSendOtp(String email, String name) {

        // generate random no.
        String otp = String.format("%06d", new SecureRandom().nextInt(1_000_000));

        if (otpDevMode) {
            log.info("========================================");
            log.info("DEV OTP");
            log.info("Email: {}", email);
            log.info("OTP: {}", otp);
            log.info("========================================");
        }

        OtpVerification verification = OtpVerification.builder()
                .email(email)
                .otp(otp)
                .expiresAt(
                        LocalDateTime.now()
                                .plusMinutes(OTP_EXPIRY_MINUTES)
                )
                .verified(false)
                .attempts(0)
                .createdDate(LocalDateTime.now())
                .build();

        otpVerificationRepository.save(verification);

        notificationService.sendOtpEmail(
                email,
                name,
                otp,
                OTP_EXPIRY_MINUTES
        );
    }

    @Override
    @Transactional
    public void verifyOtp(String email, String otp) {
        OtpVerification verification = otpVerificationRepository
                .findTopByEmailOrderByCreatedDateDesc(email)
                .orElseThrow(() -> new IllegalStateException("OTP not found."));

        if (verification.isVerified()) {
            throw new IllegalStateException("OTP has already been verified.");
        }

        if (verification.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("OTP has expired.");
        }

        if (verification.getAttempts() >= 5) {
            throw new IllegalStateException("Maximum OTP attempts exceeded.");
        }

        verification.setAttempts(verification.getAttempts() + 1);

        if (!verification.getOtp().equals(otp)) {
            otpVerificationRepository.save(verification);
            throw new IllegalArgumentException("Invalid OTP.");
        }

        verification.setVerified(true);
        otpVerificationRepository.save(verification);
    }

    @Override
    @Transactional
    public void resendOtp(String email, String name) {
        OtpVerification latest = otpVerificationRepository
                        .findTopByEmailOrderByCreatedDateDesc(email)
                        .orElse(null);

        if (latest != null) {

            LocalDateTime cooldownTime = latest.getCreatedDate().plusSeconds(RESEND_COOLDOWN_SECONDS);

            if (LocalDateTime.now().isBefore(cooldownTime)) {
                long remainingSeconds =
                        java.time.Duration.between(
                                LocalDateTime.now(),
                                cooldownTime
                        ).getSeconds();

                throw new IllegalStateException("Please wait " + remainingSeconds + " seconds before requesting another OTP.");
            }
            // Invalidate previous OTP
            latest.setVerified(true);
            otpVerificationRepository.save(latest);
        }
        generateAndSendOtp(email, name);
    }

}