package com.main.MerchantMart.utility.mapper;

import com.main.MerchantMart.payload.dto.UserDto;
import com.main.MerchantMart.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

@RequiredArgsConstructor
public class UserMapper {

    public static UserDto toDto(User user){
        UserDto userResponse=new UserDto();
        userResponse.setId(user.getId());
        userResponse.setFullUserName(user.getFullUserName());
        userResponse.setEmail(user.getEmail());
        userResponse.setPhoneNo(user.getPhoneNo());
        userResponse.setRole(user.getRole());
        userResponse.setLastLoginDate(user.getLastLoginDate());
        return  userResponse;
    }

    public static User toEntity(UserDto userDto) {
        return  User.builder()
                .fullUserName(userDto.getFullUserName())
                .email(userDto.getEmail())
                .role(userDto.getRole())
                .phoneNo(userDto.getPhoneNo())
                .lastLoginDate(userDto.getLastLoginDate())
                .build();
    }

}
