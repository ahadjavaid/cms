package com.abdulahad.cms.dto;

public class JwtResponse {
	private String token;
	private UserDto user;

	public JwtResponse(String token, UserDto user) {
		this.token = token;
		this.user = user;
	}

	public String getToken() {
		return token;
	}

	public UserDto getUser() {
		return user;
	}
}
