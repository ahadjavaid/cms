package com.abdulahad.cms.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UserSignupDto {
    @NotNull(message = "Name is required")
	@Size(min = 3, max = 100, message = "Name must be between 3 and 100 characters")
    private String name;
    
    @NotNull(message = "Email is required") 
	@Email(message = "Email must be valid") 
    private String email;
    
    @NotNull(message = "Password is required")
	@Size(min = 8, max = 100, message = "Password must be between 6 and 100 characters")
    private String password;

    @Pattern(regexp = "^\\+[0-9]{7,15}$", message = "Phone number is invalid") 
    private String phoneNumber;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
