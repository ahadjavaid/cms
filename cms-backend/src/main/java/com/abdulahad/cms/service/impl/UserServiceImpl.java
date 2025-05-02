package com.abdulahad.cms.service.impl;

import java.util.Optional;

import org.slf4j.Logger;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.abdulahad.cms.dto.JwtResponse;
import com.abdulahad.cms.dto.UserChangePasswordDto;
import com.abdulahad.cms.dto.UserDto;
import com.abdulahad.cms.dto.UserLoginDto;
import com.abdulahad.cms.dto.UserSignupDto;
import com.abdulahad.cms.entity.User;
import com.abdulahad.cms.exceptions.InvalidCredentialsException;
import com.abdulahad.cms.exceptions.UserAlreadyExistsException;
import com.abdulahad.cms.exceptions.UsernameNotFoundException;
import com.abdulahad.cms.repository.UserRepository;
import com.abdulahad.cms.security.JwtUtil;
import com.abdulahad.cms.service.UserService;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger log = org.slf4j.LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
	    this.jwtUtil = jwtUtil;
	    this.authenticationManager = authenticationManager;
    }

    @Override
    public JwtResponse signupUser(UserSignupDto signupDto) {
        log.info("Attempting to sign up user with email: {}", signupDto.getEmail());
        if(userRepository.findByEmail(signupDto.getEmail()).isPresent() || userRepository.findByPhoneNumber(signupDto.getPhoneNumber()).isPresent()) {
            log.warn("Signup failed: User already exists with email {} or phone number {}", signupDto.getEmail(), signupDto.getPhoneNumber());
            throw new UserAlreadyExistsException("User with this email or phone number already exist");
        }

        User user = new User();
        user.setName(signupDto.getName());
        user.setEmail(signupDto.getEmail());
        user.setPhoneNumber(signupDto.getPhoneNumber());
        user.setPassword(passwordEncoder.encode(signupDto.getPassword()));
        user = userRepository.save(user);
        log.info("User signed up successfully with ID: {}", user.getId());

        String token = jwtUtil.generateToken(user.getEmail());

        UserDto userDto =  new UserDto(
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getPhoneNumber()
        );

        return new JwtResponse(
            token,
            userDto
        );
    }

    @Override
    public JwtResponse loginUser(UserLoginDto loginDto) {
        log.info("Attempting login for user: {}", loginDto.getEmailOrPhone());
        Optional<User> optionalUser = userRepository.findByEmail(loginDto.getEmailOrPhone());
        if(optionalUser.isEmpty()) {
            optionalUser = userRepository.findByPhoneNumber(loginDto.getEmailOrPhone());
            if(optionalUser.isEmpty()) {
                log.warn("Login failed: User not found with identifier: {}", loginDto.getEmailOrPhone());
                throw new UsernameNotFoundException("Invalid email or phone number");
            }
        }
        User user = optionalUser.get();
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginDto.getEmailOrPhone(),
                            loginDto.getPassword()
                    )
            );
            log.info("User authenticated successfully: {}", loginDto.getEmailOrPhone());
        } catch (AuthenticationException e) {
            log.warn("Login failed: Invalid credentials for user: {}", loginDto.getEmailOrPhone());
            throw new InvalidCredentialsException("Invalid email or password");
        }

        String token = jwtUtil.generateToken(user.getEmail());

        UserDto userDto =  new UserDto(
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getPhoneNumber()
        );

        return new JwtResponse(
            token,
            userDto
        );
    }

    @Override
    public UserDto changePassword(UserChangePasswordDto userChangePasswordDto, int userId) {
        log.info("Attempting to change password for user ID: {}", userId);
        Optional<User> optionalUser = userRepository.findById(userId);
        if(optionalUser.isEmpty()) {
            log.warn("Password change failed: User not found with ID: {}", userId);
            throw new UsernameNotFoundException("User not found");
        }

        User user = optionalUser.get();
        if(!passwordEncoder.matches(userChangePasswordDto.getCurrentPassword(), user.getPassword())) {
            log.warn("Password change failed: Invalid old password for user ID: {}", userId);
            throw new InvalidCredentialsException("Invalid old password");
        }

        user.setPassword(passwordEncoder.encode(userChangePasswordDto.getNewPassword()));
        userRepository.save(user);
        log.info("Password changed successfully for user ID: {}", userId);
        return new UserDto(
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getPhoneNumber()
        );
    }
}