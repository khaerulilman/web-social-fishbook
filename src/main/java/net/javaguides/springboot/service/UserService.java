package net.javaguides.springboot.service;

import org.springframework.security.core.userdetails.UserDetailsService;

import net.javaguides.springboot.model.User;
import net.javaguides.springboot.web.dto.UserRegistrationDto;

import java.util.List;

public interface UserService extends UserDetailsService {

	User saveUser(UserRegistrationDto registrationDto);

	User findUserByEmail(String email);

	List<UserRegistrationDto> findAllUsers();

	void updateUser(UserRegistrationDto userDto);
}

