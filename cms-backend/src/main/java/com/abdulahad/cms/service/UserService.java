package com.abdulahad.cms.service;

import com.abdulahad.cms.dao.UserRepository;
import com.abdulahad.cms.entity.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    public User registerUser(String email, String phoneNumber, String password) {
        if(userRepository.findByEmail(email).isPresent() || userRepository.findByPhoneNumber(phoneNumber).isPresent()) {
            throw new RuntimeException("User already exist with this email or phone number");
        }

       User user = new User();
        user.setEmail(email);
        user.setPhoneNumber(phoneNumber);
        user.setPassword(passwordEncoder.encode(password));
        return userRepository.save(user);
    }
}