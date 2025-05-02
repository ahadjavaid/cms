package com.abdulahad.cms.service;

import com.abdulahad.cms.dto.JwtResponse;
import com.abdulahad.cms.dto.UserChangePasswordDto;
import com.abdulahad.cms.dto.UserDto;
import com.abdulahad.cms.dto.UserLoginDto;
import com.abdulahad.cms.dto.UserSignupDto;

public interface UserService {
    JwtResponse signupUser(UserSignupDto signupDto);
    JwtResponse loginUser(UserLoginDto loginDto);
    UserDto changePassword(UserChangePasswordDto userChangePasswordDto, int userId);
}
