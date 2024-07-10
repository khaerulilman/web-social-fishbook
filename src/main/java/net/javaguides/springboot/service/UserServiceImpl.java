package net.javaguides.springboot.service;

import net.javaguides.springboot.model.User;
import net.javaguides.springboot.repository.UserRepository;
import net.javaguides.springboot.web.dto.UserRegistrationDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;

	@Autowired
	public UserServiceImpl(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public User saveUser(UserRegistrationDto registrationDto) {
		User user = new User();
		user.setUsername(registrationDto.getUsername());
		user.setEmail(registrationDto.getEmail());
		user.setPassword(registrationDto.getPassword()); // No password encryption
		return userRepository.save(user);
	}

	@Override
	public User findUserByEmail(String email) {
		return userRepository.findByEmail(email);
	}

	@Override
	public List<UserRegistrationDto> findAllUsers() {
		List<User> users = userRepository.findAll();
		return users.stream().map(this::convertEntityToDto).collect(Collectors.toList());
	}

	@Override
	public void updateUser(UserRegistrationDto userDto) {
		User existingUser = userRepository.findByEmail(userDto.getEmail());
		if (existingUser != null) {
			existingUser.setUsername(userDto.getUsername());
			existingUser.setPassword(userDto.getPassword()); // No password encryption
			userRepository.save(existingUser);
		}
	}

	private UserRegistrationDto convertEntityToDto(User user) {
		UserRegistrationDto userDto = new UserRegistrationDto();
		userDto.setUsername(user.getUsername());
		userDto.setEmail(user.getEmail());
		return userDto;
	}

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		User user = userRepository.findByEmail(email);
		if (user == null) {
			throw new UsernameNotFoundException("Invalid email or password.");
		}
		return org.springframework.security.core.userdetails.User.builder()
				.username(user.getEmail())
				.password(user.getPassword())
				.authorities(Collections.emptyList())
				.build();
	}
}


