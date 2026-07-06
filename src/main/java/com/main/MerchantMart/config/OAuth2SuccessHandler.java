package com.main.MerchantMart.config;

import com.main.MerchantMart.domain.Role;
import com.main.MerchantMart.entity.User;
import com.main.MerchantMart.repository.UserRepository;
import com.main.MerchantMart.utility.contants.AuthConstants;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler{

    private final JwtProvider   jwtProvider;

    private final UserRepository  userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        User user=userRepository.findByEmail(email).orElse(null);
        if(user == null){
            user = new User();
            user.setEmail(email);
            user.setProvider(AuthConstants.PROVIDER_GOOGLE);
            user.setFullUserName(name);
            user.setRole(Role.ROLE_USER);
        }
        user.setLastLoginDate(LocalDateTime.now());
        String jwt=jwtProvider.generateToken(authentication);

        userRepository.save(user);

        response.sendRedirect("http://localhost:5173?token="+jwt);
    }
}
