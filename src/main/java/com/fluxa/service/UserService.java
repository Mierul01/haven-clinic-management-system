package com.fluxa.service;

import com.fluxa.model.User;
import com.fluxa.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    @Transactional
    public User register(String fullName, String email, String password, String companyName) {
        if (userRepository.existsByEmail(email.toLowerCase().trim())) {
            throw new IllegalArgumentException("Email already registered");
        }
        User user = new User();
        user.setFullName(fullName.trim());
        user.setEmail(email.toLowerCase().trim());
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setCompanyName(companyName == null ? "" : companyName.trim());
        user.setPlan("CLINIC");
        return userRepository.save(user);
    }

    @Transactional
    public User updateProfile(User user, String fullName, String companyName) {
        user.setFullName(fullName.trim());
        user.setCompanyName(companyName == null ? "" : companyName.trim());
        return userRepository.save(user);
    }
}
