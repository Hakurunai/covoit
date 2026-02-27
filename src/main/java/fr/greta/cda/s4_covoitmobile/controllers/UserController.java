package fr.greta.cda.s4_covoitmobile.controllers;

import fr.greta.cda.s4_covoitmobile.data.EAccountRole;
import fr.greta.cda.s4_covoitmobile.data.EAccountStatus;
import fr.greta.cda.s4_covoitmobile.dto.user.CreateUserRequest;
import fr.greta.cda.s4_covoitmobile.dto.user.CreateUserResponse;
import fr.greta.cda.s4_covoitmobile.dto.user.GetAllUserResponse;
import fr.greta.cda.s4_covoitmobile.dto.user.GetPersonByIdResponse;
import fr.greta.cda.s4_covoitmobile.dto.userprofile.CreateUserProfileRequest;
import fr.greta.cda.s4_covoitmobile.dto.userprofile.CreateUserProfileResponse;
import fr.greta.cda.s4_covoitmobile.dto.userprofile.UpdateProfileRequest;
import fr.greta.cda.s4_covoitmobile.models.User;
import fr.greta.cda.s4_covoitmobile.models.UserProfile;
import fr.greta.cda.s4_covoitmobile.security.UserDetailsImpl;
import fr.greta.cda.s4_covoitmobile.security.annotations.IsAdmin;
import fr.greta.cda.s4_covoitmobile.security.annotations.IsUser;
import fr.greta.cda.s4_covoitmobile.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserController
{
	private final UserService userService;
	
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
				.email(newUser.getEmail())
				.build()
		);
	}
	
	@PostMapping("/person")
	@IsUser
	public ResponseEntity<CreateUserProfileResponse> setupProfile(
		@Valid @RequestBody CreateUserProfileRequest request,
		@RequestParam(required = false) Long targetUserId,
		@AuthenticationPrincipal UserDetailsImpl currentUser)
	{
		if (currentUser == null)
		{
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid session or user unknown");
		}
		
		UserProfile newProfil = userService.completeProfile(targetUserId, currentUser, request);
		return ResponseEntity.status(HttpStatus.CREATED).body(
			CreateUserProfileResponse.builder()
				.firstName(newProfil.getFirstname())
				.lastName(newProfil.getLastname())
				.phone(newProfil.getPhone())
				.accountStatus(newProfil.getUser().getAccountStatus().getName().name())
				.build()
		);
	}
	
	@GetMapping("/persons")
	@IsAdmin
	public ResponseEntity<List<GetAllUserResponse>> getAllUser()
	{
		List<User> allUsers = userService.getAllUsersData();
		
		var response = allUsers.stream()
			.map(GetAllUserResponse::new)
			.toList();
		return ResponseEntity.ok(response);
	}
	
	@GetMapping("/persons/{id}")
	@IsUser
	public ResponseEntity<GetPersonByIdResponse> getPersonById(@PathVariable Long id)
	{
		User userData = userService.getUserDetail(id);
		
		var response = new GetPersonByIdResponse(userData);
		return ResponseEntity.ok(response);
	}
	
	@PatchMapping("/persons/{id}")
	@IsUser
	public ResponseEntity<CreateUserProfileResponse> updateProfile(
		@PathVariable Long id,
		@Valid @RequestBody UpdateProfileRequest request,
		@AuthenticationPrincipal UserDetailsImpl currentUser)
	{
		checkAccessRightsOnId(currentUser, id);
		
		UserProfile newProfil = userService.patchProfile(id, request);
		return ResponseEntity.status(HttpStatus.CREATED).body(
			CreateUserProfileResponse.builder()
				.firstName(newProfil.getFirstname())
				.lastName(newProfil.getLastname())
				.phone(newProfil.getPhone())
				.accountStatus(newProfil.getUser().getAccountStatus().getName().name())
				.build()
		);
	}
	
	@DeleteMapping("/persons/{id}")
	@IsUser
	public ResponseEntity<Void> anonymizeUser(@PathVariable Long id,
		@AuthenticationPrincipal UserDetailsImpl currentUser)
	{
		checkAccessRightsOnId(currentUser, id);
		
		userService.anonymizeUser(id);
		return ResponseEntity.noContent().build();
	}
	
	private void checkAccessRightsOnId(UserDetailsImpl userDetails, Long targetedId)
	{
		if (!userDetails.canAccess(targetedId))
		{
			throw new AuthorizationDeniedException("Your rights did not allow you to access this resource");
		}
	}
}
