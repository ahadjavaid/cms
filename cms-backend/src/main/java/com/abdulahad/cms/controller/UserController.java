package com.abdulahad.cms.controller;

import com.abdulahad.cms.dto.UserDto;
import com.abdulahad.cms.dto.UserLoginDto;
import com.abdulahad.cms.dto.UserSignupDto;
import com.abdulahad.cms.entity.User;
import com.abdulahad.cms.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/signup")
    public ResponseEntity<UserDto> signup(@RequestBody UserSignupDto signupDto) {
        return new ResponseEntity<>(userService.signupUser(signupDto), HttpStatus.OK);
    }

//    @PostMapping("/login")
//    public  ResponseEntity<UserDto> login(@RequestBody UserLoginDto loginDto) {
//        return new ResponseEntity<>(userService.loginUser(loginDto),HttpStatus.OK);
//    }

    @PostMapping("/login")
    public UserDto login(@RequestBody UserLoginDto loginRequest) {
        return userService.loginUser(loginRequest);
    }
}
