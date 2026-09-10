package com.main.MerchantMart.service.impl;

import com.main.MerchantMart.config.JwtProvider;
import com.main.MerchantMart.domain.Role;
import com.main.MerchantMart.entity.User;
import com.main.MerchantMart.payload.dto.UserDto;
import com.main.MerchantMart.utility.contants.ExceptionMessageConstants;
import com.main.MerchantMart.repository.UserRepository;
import com.main.MerchantMart.service.UserService;
import com.main.MerchantMart.utility.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private  final JwtProvider jwtProvider;
    private  final UserRepository userRepository;
    @Override
    public User getCurrentUser() {
        String email=SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        return userRepository.findByEmail(email).orElseThrow(()-> new UsernameNotFoundException(ExceptionMessageConstants.USER_NOT_FOUND));
    }

    @Override
    public User getUserFromJwtToken(String jwt) {
        String email= jwtProvider.getEmailFromToken(jwt);
        return userRepository.findByEmail(email).orElseThrow(()-> new UsernameNotFoundException(ExceptionMessageConstants.USER_NOT_FOUND));
    }

    /// SUPER ADMIN METHODS///

    @Override
    public List<UserDto> getAllStoreAdmins() {
        return userRepository.findByRole(Role.ROLE_STORE_ADMIN)
                .stream()
                .map(UserMapper::toDto)
                .toList();
    }

}
