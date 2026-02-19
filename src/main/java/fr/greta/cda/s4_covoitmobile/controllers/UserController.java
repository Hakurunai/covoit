package fr.greta.cda.s4_covoitmobile.controllers;

import fr.greta.cda.s4_covoitmobile.data.EAccountRole;
import fr.greta.cda.s4_covoitmobile.data.EAccountStatus;
import fr.greta.cda.s4_covoitmobile.dtos.UserCreatedResponse;
import fr.greta.cda.s4_covoitmobile.dtos.auth.LoginRequest;
import fr.greta.cda.s4_covoitmobile.models.User;
import fr.greta.cda.s4_covoitmobile.security.annotations.IsUser;
import fr.greta.cda.s4_covoitmobile.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserController
{
	private final UserService userService;
	
	@Autowired
	public UserController(final UserService userService) {this.userService = userService;}
	
	@PostMapping("/register")
	public ResponseEntity<UserCreatedResponse> createUser(@RequestBody LoginRequest loginRequest)
	{
		User newUser = userService.registerNewUser(
			loginRequest.getEmail(),
			loginRequest.getPassword(),
			List.of(EAccountRole.ROLE_USER),
			EAccountStatus.PENDING);
		
		return ResponseEntity.status(HttpStatus.CREATED).body(
			UserCreatedResponse.builder()
				.id(newUser.getId())
				.roles(newUser.getRoles().stream()
					.map(roleEntity -> roleEntity.getName().name())
					.toList())
				.status(newUser.getAccountStatus().getName().name())
				.build()
		);
	}
	
	@GetMapping("/me")
	@IsUser
	public ResponseEntity<String> testAuth()
	{
		return ResponseEntity.ok("If you see this message, you are authenticated and your role is enough to see it");
	}
}
