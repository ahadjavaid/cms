package com.abdulahad.cms.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.abdulahad.cms.dto.JwtResponse;
import com.abdulahad.cms.dto.UserChangePasswordDto;
import com.abdulahad.cms.dto.UserDto;
import com.abdulahad.cms.dto.UserLoginDto;
import com.abdulahad.cms.dto.UserSignupDto;
import com.abdulahad.cms.security.JwtUser;
import com.abdulahad.cms.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/signup")
    public ResponseEntity<JwtResponse> signup(@RequestBody @Valid UserSignupDto signupDto) {
        return new ResponseEntity<>(userService.signupUser(signupDto), HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody @Valid UserLoginDto loginRequest) {
        return new ResponseEntity<>(userService.loginUser(loginRequest), HttpStatus.OK);
    }

    @PostMapping("/change-password")
    public ResponseEntity<UserDto> changePassword(@RequestBody @Valid UserChangePasswordDto userChangePasswordDto, @AuthenticationPrincipal JwtUser jwtUser) {
        UserDto userDto = userService.changePassword(userChangePasswordDto, jwtUser.getUserId());
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }
}
