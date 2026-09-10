package com.main.MerchantMart.controller;

import com.main.MerchantMart.entity.User;
import com.main.MerchantMart.payload.dto.UserDto;
import com.main.MerchantMart.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<String> userProfile() {
        return ResponseEntity.ok("profile");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/store-admins")
    public ResponseEntity<List<UserDto>> getAllStoreAdmins() {
        return ResponseEntity.ok(userService.getAllStoreAdmins());
    }
}