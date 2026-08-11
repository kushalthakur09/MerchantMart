package com.main.MerchantMart.controller;

import com.main.MerchantMart.payload.dto.ChangePasswordDto;
import com.main.MerchantMart.payload.dto.ProfileUpdateDto;
import com.main.MerchantMart.payload.dto.UserDto;
import com.main.MerchantMart.service.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping
    public ResponseEntity<UserDto> getProfile() {
        return ResponseEntity.ok(
                profileService.getProfile()
        );
    }

    @PutMapping
    public ResponseEntity<UserDto> updateProfile(
            @Valid @RequestBody ProfileUpdateDto dto) {

        return ResponseEntity.ok(
                profileService.updateProfile(dto)
        );
    }

    @PutMapping("/password")
    public ResponseEntity<Void> changePassword(@Valid @RequestBody ChangePasswordDto dto) {
        profileService.changePassword(dto);
        return ResponseEntity.noContent().build();
    }
}