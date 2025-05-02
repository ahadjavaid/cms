package com.abdulahad.cms.service;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

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
import com.abdulahad.cms.service.impl.UserServiceImpl;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private UserServiceImpl userService;

    private UserSignupDto signupDto;
    private UserLoginDto loginDto;
    private UserChangePasswordDto changePasswordDto;
    private User user;

    @BeforeEach
    void setUp() {
        signupDto = new UserSignupDto();
        signupDto.setName("Test User");
        signupDto.setEmail("test@example.com");
        signupDto.setPhoneNumber("+1234567890");
        signupDto.setPassword("password123");

        loginDto = new UserLoginDto();
        loginDto.setEmailOrPhone("test@example.com");
        loginDto.setPassword("password123");

        changePasswordDto = new UserChangePasswordDto();
        changePasswordDto.setCurrentPassword("password123");
        changePasswordDto.setNewPassword("newPassword456");

        user = new User();
        user.setId(1);
        user.setName("Test User");
        user.setEmail("test@example.com");
        user.setPhoneNumber("+1234567890");
        user.setPassword("encodedPassword123");
    }

    @Test
    void signupUser_Success() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(userRepository.findByPhoneNumber(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword123");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(jwtUtil.generateToken(anyString())).thenReturn("mockToken");

        JwtResponse response = userService.signupUser(signupDto);

        assertNotNull(response);
        assertEquals("mockToken", response.getToken());
        assertNotNull(response.getUser());
        assertEquals(user.getId(), response.getUser().getId());
        assertEquals(user.getEmail(), response.getUser().getEmail());

        verify(userRepository, times(1)).findByEmail("test@example.com");
        verify(userRepository, times(1)).findByPhoneNumber("+1234567890");
        verify(passwordEncoder, times(1)).encode("password123");
        verify(userRepository, times(1)).save(any(User.class));
        verify(jwtUtil, times(1)).generateToken("test@example.com");
    }

    @Test
    void signupUser_UserAlreadyExists_Email() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        assertThrows(UserAlreadyExistsException.class, () -> userService.signupUser(signupDto));

        verify(userRepository, times(1)).findByEmail("test@example.com");
        verify(userRepository, never()).findByPhoneNumber(anyString());
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
        verify(jwtUtil, never()).generateToken(anyString());
    }

     @Test
    void signupUser_UserAlreadyExists_Phone() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(userRepository.findByPhoneNumber(anyString())).thenReturn(Optional.of(user));

        assertThrows(UserAlreadyExistsException.class, () -> userService.signupUser(signupDto));

        verify(userRepository, times(1)).findByEmail("test@example.com");
        verify(userRepository, times(1)).findByPhoneNumber("+1234567890");
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
        verify(jwtUtil, never()).generateToken(anyString());
    }

    @Test
    void loginUser_Success_Email() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        // Mock successful authentication
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(null);
        when(jwtUtil.generateToken(anyString())).thenReturn("mockToken");

        JwtResponse response = userService.loginUser(loginDto);

        assertNotNull(response);
        assertEquals("mockToken", response.getToken());
        assertNotNull(response.getUser());
        assertEquals(user.getId(), response.getUser().getId());

        verify(userRepository, times(1)).findByEmail("test@example.com");
        verify(userRepository, never()).findByPhoneNumber(anyString());
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtil, times(1)).generateToken("test@example.com");
    }

    @Test
    void loginUser_Success_Phone() {
        loginDto.setEmailOrPhone("+1234567890");
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(userRepository.findByPhoneNumber(anyString())).thenReturn(Optional.of(user));
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(null);
        when(jwtUtil.generateToken(anyString())).thenReturn("mockToken");

        JwtResponse response = userService.loginUser(loginDto);

        assertNotNull(response);
        assertEquals("mockToken", response.getToken());
        assertEquals(user.getId(), response.getUser().getId());

        verify(userRepository, times(1)).findByEmail("+1234567890");
        verify(userRepository, times(1)).findByPhoneNumber("+1234567890");
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtil, times(1)).generateToken("test@example.com");
    }

    @Test
    void loginUser_UserNotFound() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(userRepository.findByPhoneNumber(anyString())).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userService.loginUser(loginDto));

        verify(userRepository, times(1)).findByEmail("test@example.com");
        verify(userRepository, times(1)).findByPhoneNumber("test@example.com");
        verify(authenticationManager, never()).authenticate(any());
        verify(jwtUtil, never()).generateToken(anyString());
    }

    @Test
    void loginUser_InvalidCredentials() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        // Mock failed authentication
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThrows(InvalidCredentialsException.class, () -> userService.loginUser(loginDto));

        verify(userRepository, times(1)).findByEmail("test@example.com");
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtil, never()).generateToken(anyString());
    }

    @Test
    void changePassword_Success() {
        int userId = 1;
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", "encodedPassword123")).thenReturn(true);
        when(passwordEncoder.encode("newPassword456")).thenReturn("encodedNewPassword456");
        when(userRepository.save(any(User.class))).thenReturn(user); // Mock save

        UserDto result = userService.changePassword(changePasswordDto, userId);

        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals("test@example.com", result.getEmail());
        verify(userRepository, times(1)).save(user);
        verify(passwordEncoder, times(1)).matches("password123", "encodedPassword123");
        verify(passwordEncoder, times(1)).encode("newPassword456");
    }

    @Test
    void changePassword_UserNotFound() {
        int userId = 99;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userService.changePassword(changePasswordDto, userId));

        verify(userRepository, times(1)).findById(userId);
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void changePassword_InvalidOldPassword() {
        int userId = 1;
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", "encodedPassword123")).thenReturn(false); // Mock incorrect old password

        assertThrows(InvalidCredentialsException.class, () -> userService.changePassword(changePasswordDto, userId));

        verify(userRepository, times(1)).findById(userId);
        verify(passwordEncoder, times(1)).matches("password123", "encodedPassword123");
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }
}
