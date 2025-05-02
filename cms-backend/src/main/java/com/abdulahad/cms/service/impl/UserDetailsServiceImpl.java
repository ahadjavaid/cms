package com.abdulahad.cms.service.impl;

import com.abdulahad.cms.entity.User;
import com.abdulahad.cms.repository.UserRepository;
import com.abdulahad.cms.security.JwtUser;
import java.util.Optional;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	private final UserRepository userRepository;

	public UserDetailsServiceImpl(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Optional<User> userOptional = userRepository.findByEmail(username);
		if (userOptional.isEmpty()) {
			userOptional = userRepository.findByPhoneNumber(username);
		}

		User user = userOptional.orElseThrow(() ->
			new UsernameNotFoundException("User not found with email/phone: " + username));

		return new JwtUser(
			user.getEmail(),
			user.getPassword(),
			user.getId()
		);
	}
}
