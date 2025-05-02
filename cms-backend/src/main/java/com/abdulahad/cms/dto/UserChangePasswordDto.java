package com.abdulahad.cms.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class UserChangePasswordDto {

    @NotNull(message = "Current Password is required")
    private  String currentPassword;

    @NotNull(message = "New Password is required")
	@Size(min = 8, max = 100, message = "Password must be between 6 and 100 characters")
    private  String newPassword;

    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
