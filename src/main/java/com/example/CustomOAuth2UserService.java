package com.example;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserService userService;

    public CustomOAuth2UserService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        try {
            System.out.println("Loading OAuth2 user...");
            OAuth2User oAuth2User = super.loadUser(userRequest);
            System.out.println("OAuth2User attributes: " + oAuth2User.getAttributes());
            
            String email = oAuth2User.getAttribute("email");
            String name = oAuth2User.getAttribute("name");
            String googleId = oAuth2User.getName();
            
            System.out.println("Processing user - Email: " + email + ", Name: " + name + ", GoogleID: " + googleId);
            
            if (email == null) {
                throw new OAuth2AuthenticationException("Email not found in OAuth2 user attributes");
            }
            
            userService.processOAuthPostLogin(email, name, googleId);
            System.out.println("OAuth2 user processing completed successfully");
            
            return oAuth2User;
        } catch (Exception e) {
            System.err.println("Error in OAuth2 user service: " + e.getMessage());
            e.printStackTrace();
            throw new OAuth2AuthenticationException("OAuth2 authentication failed: " + e.getMessage());
        }
    }
}