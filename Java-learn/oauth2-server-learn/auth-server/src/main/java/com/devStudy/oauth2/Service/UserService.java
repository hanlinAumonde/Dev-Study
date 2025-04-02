package com.devStudy.oauth2.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.devStudy.oauth2.dao.UserRepository;
import com.devStudy.oauth2.entity.User;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserService implements UserDetailsService{

	private final UserRepository userRepository;

	@Autowired
	public UserService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException("user not found"));
		return org.springframework.security.core.userdetails.User
				.withUsername(user.getEmail())
				.password(user.getPassword())
				.authorities("ROLE_USER")
				.build();
	}

	public Map<String,Object> loadUserInfoByEmail(String email) {
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException("user not found"));
		Map<String, Object> userInfo = new HashMap<>();
		userInfo.put("email", user.getEmail());
		userInfo.put("firstName", user.getFirstName());
		userInfo.put("lastName", user.getLastName());
		userInfo.put("gender", user.getGender());
		userInfo.put("dateOfBirth", user.getDateOfBirth());
		userInfo.put("phone", user.getPhone());
		userInfo.put("address", user.getAddress());

		return userInfo;
	}
}
