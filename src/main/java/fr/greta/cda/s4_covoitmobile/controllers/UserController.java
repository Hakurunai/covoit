package fr.greta.cda.s4_covoitmobile.controllers;

import fr.greta.cda.s4_covoitmobile.data.EAccountRole;
import fr.greta.cda.s4_covoitmobile.data.EAccountStatus;
import fr.greta.cda.s4_covoitmobile.dto.user.CreateUserRequest;
import fr.greta.cda.s4_covoitmobile.dto.user.CreateUserResponse;
import fr.greta.cda.s4_covoitmobile.dto.user.GetAllUserResponse;
import fr.greta.cda.s4_covoitmobile.dto.userprofile.CreateUserProfileRequest;
import fr.greta.cda.s4_covoitmobile.dto.userprofile.CreateUserProfileResponse;
import fr.greta.cda.s4_covoitmobile.models.User;
import fr.greta.cda.s4_covoitmobile.security.UserDetailsImpl;
import fr.greta.cda.s4_covoitmobile.security.annotations.IsAdmin;
import fr.greta.cda.s4_covoitmobile.security.annotations.IsUser;
import fr.greta.cda.s4_covoitmobile.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@RestController
public class UserController
{
	private final UserService userService;
	
	@Autowired
	public UserController(final UserService userService) {this.userService = userService;}
	
	@PostMapping("/register")
	public ResponseEntity<CreateUserResponse> createUser(@Valid @RequestBody CreateUserRequest request)
	{
		User newUser = userService.registerNewUser(
			request.getEmail(),
			request.getPassword(),
			List.of(EAccountRole.ROLE_USER),
			EAccountStatus.PENDING);
		
		return ResponseEntity.status(HttpStatus.CREATED).body(
			CreateUserResponse.builder()
				.id(newUser.getId())
				.roles(newUser.getRoles().stream()
					.map(roleEntity -> roleEntity.getName().name())
					.toList())
				.status(newUser.getAccountStatus().getName().name())
				.build()
		);
	}
	
	@PostMapping("/person")
	@IsUser
	public ResponseEntity<CreateUserProfileResponse> setupProfile(@Valid @RequestBody CreateUserProfileRequest request,
		Authentication authentication)
	{
		Long userId = Optional.ofNullable((UserDetailsImpl) authentication.getPrincipal())
			.map(UserDetailsImpl::getId)
			.orElseThrow(
				() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User ID not found in security context"));
		
		userService.completeProfile(userId, request);
		
		CreateUserProfileResponse response = new CreateUserProfileResponse();
		response.setData("Your account is now activated");
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}
	
	@GetMapping("/persons")
	@IsAdmin
	public ResponseEntity<List<GetAllUserResponse>> getAllUser()
	{
		return ResponseEntity.ok(userService.getAllUsersData());
	}
	
	@GetMapping("/me")
	@IsUser
	public ResponseEntity<String> testAuth()
	{
		return ResponseEntity.ok("If you see this message, you are authenticated and your role is enough to see it");
	}
	
	@GetMapping("/meAdmin")
	@IsAdmin
	public ResponseEntity<String> testAuthAdmin()
	{
		return ResponseEntity.ok("If you see this message, you are authenticated and you are an admin");
	}
}
