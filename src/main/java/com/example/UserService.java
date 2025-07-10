package com.example;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public void processOAuthPostLogin(String email, String name, String googleId) {
        userRepository.findByEmail(email)
                .ifPresentOrElse(
                        user -> {},
                        () -> {
                            User newUser = new User();
                            newUser.setEmail(email);
                            newUser.setName(name);
                            newUser.setGoogleId(googleId);
                            userRepository.save(newUser);
                        }
                );
    }
}