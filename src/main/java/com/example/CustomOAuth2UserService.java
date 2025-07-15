package com.example;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private static final Logger logger = LoggerFactory.getLogger(CustomOAuth2UserService.class);

    private final UserService userService;

    public CustomOAuth2UserService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        try {
            logger.info("Loading OAuth2 user...");
            OAuth2User oAuth2User = super.loadUser(userRequest);
            logger.info("OAuth2User attributes: {}", oAuth2User.getAttributes());
            
            String email = oAuth2User.getAttribute("email");
            String name = oAuth2User.getAttribute("name");
            String googleId = oAuth2User.getName();
            
            logger.info("Processing user - Email: {}, Name: {}, GoogleID: {}", email, name, googleId);
            
            if (email == null) {
                logger.error("Email not found in OAuth2 user attributes for user: {}", oAuth2User.getName());
                throw new OAuth2AuthenticationException("Email not found in OAuth2 user attributes");
            }
            
            userService.processOAuthPostLogin(email, name, googleId);
            logger.info("OAuth2 user processing completed successfully for user: {}", email);
            
            return oAuth2User;
        } catch (Exception e) {
            logger.error("Error in OAuth2 user service: {}", e.getMessage(), e);
            throw new OAuth2AuthenticationException("OAuth2 authentication failed: " + e.getMessage());
        }
    }
}