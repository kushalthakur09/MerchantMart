package com.main.MerchantMart.config;

import com.main.MerchantMart.domain.Role;
import com.main.MerchantMart.entity.User;
import com.main.MerchantMart.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class AdminInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

        if (userRepository.existsByRole(Role.ROLE_ADMIN)) {
            return;
        }

        User admin = User.builder()
                .fullUserName("Super Admin")
                .email("admin@merchantmart.com")
                .password(passwordEncoder.encode("Admin@123"))
                .phoneNo("9999999999")
                .role(Role.ROLE_ADMIN)
                .lastLoginDate(LocalDateTime.now())
                .provider("LOCAL")
                .build();

        userRepository.save(admin);

        System.out.println("====================================");
        System.out.println("Default Admin Created Successfully");
        System.out.println("Email : admin@merchantmart.com");
        System.out.println("Password : Admin@123");
        System.out.println("====================================");
    }
}