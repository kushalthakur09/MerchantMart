package com.main.MerchantMart.service.impl;

import com.main.MerchantMart.entity.OtpVerification;
import com.main.MerchantMart.repository.OtpVerificationRepository;
import com.main.MerchantMart.service.NotificationService;
import com.main.MerchantMart.service.OtpService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OtpServiceImpl implements OtpService {

    private static final int OTP_EXPIRY_MINUTES = 5;

    private final OtpVerificationRepository otpVerificationRepository;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public void generateAndSendOtp(String email, String name) {

        // generate random no.
        String otp = String.format(
                "%06d",
                new SecureRandom().nextInt(1_000_000)
        );

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

        OtpVerification verification =
                otpVerificationRepository
                        .findTopByEmailOrderByCreatedDateDesc(email)
                        .orElseThrow(() ->
                                new IllegalStateException(
                                        "OTP not found."
                                )
                        );

        if (verification.isVerified()) {
            throw new IllegalStateException(
                    "OTP has already been verified."
            );
        }

        if (verification.getExpiresAt()
                .isBefore(LocalDateTime.now())) {

            throw new IllegalStateException(
                    "OTP has expired."
            );
        }

        if (verification.getAttempts() >= 5) {
            throw new IllegalStateException(
                    "Maximum OTP attempts exceeded."
            );
        }

        verification.setAttempts(
                verification.getAttempts() + 1
        );

        if (!verification.getOtp().equals(otp)) {
            otpVerificationRepository.save(verification);

            throw new IllegalArgumentException(
                    "Invalid OTP."
            );
        }

        verification.setVerified(true);

        otpVerificationRepository.save(verification);
    }
}