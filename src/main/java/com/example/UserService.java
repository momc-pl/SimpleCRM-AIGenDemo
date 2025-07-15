package com.example;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public void processOAuthPostLogin(String email, String name, String googleId) {
        userRepository.findByEmail(email)
                .ifPresentOrElse(
                        user -> logger.info("User already exists: {}", user.getEmail()),
                        () -> {
                            User newUser = new User();
                            newUser.setEmail(email);
                            newUser.setName(name);
                            newUser.setGoogleId(googleId);
                            userRepository.save(newUser);
                            logger.info("New user saved: {}", newUser.getEmail());
                        }
                );
    }
}