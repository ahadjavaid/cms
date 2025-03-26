package com.abdulahad.cms.service;

import com.abdulahad.cms.dto.UserDto;
import com.abdulahad.cms.dto.UserLoginDto;
import com.abdulahad.cms.dto.UserSignupDto;
import com.abdulahad.cms.repository.UserRepository;
import com.abdulahad.cms.entity.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDto signupUser(UserSignupDto signupDto) {
        if(userRepository.findByEmail(signupDto.getEmail()).isPresent() || userRepository.findByPhoneNumber(signupDto.getPhoneNo()).isPresent()) {
            throw new RuntimeException("User already exist with this email or phone number");
        }

        User user = new User();
        user.setName(signupDto.getName());
        user.setEmail(signupDto.getEmail());
        user.setPhoneNumber(signupDto.getPhoneNo());
        user.setPassword(passwordEncoder.encode(signupDto.getPassword()));
        user = userRepository.save(user);
        return new UserDto(
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getPhoneNumber()
        );
    }

    @Override
    public UserDto loginUser(UserLoginDto loginDto) {
        Optional<User> optionalUser = userRepository.findByEmail(loginDto.getEmailOrPhone());
        if(optionalUser.isEmpty()) {
            optionalUser = userRepository.findByPhoneNumber(loginDto.getEmailOrPhone());
            if(optionalUser.isEmpty()) {
                throw new RuntimeException("Invalid email or phone number");
            }
        }
        User user = optionalUser.get();
        if(!passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPhoneNumber()
        );
    }
}