package com.abdulahad.cms.service;

import com.abdulahad.cms.dto.UserDto;
import com.abdulahad.cms.dto.UserLoginDto;
import com.abdulahad.cms.dto.UserSignupDto;

public interface UserService {
    UserDto signupUser(UserSignupDto signupDto);
    UserDto loginUser(UserLoginDto loginDto);
}
