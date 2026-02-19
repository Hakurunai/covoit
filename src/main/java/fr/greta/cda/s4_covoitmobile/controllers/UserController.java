package fr.greta.cda.s4_covoitmobile.controllers;

import fr.greta.cda.s4_covoitmobile.data.EAccountRole;
import fr.greta.cda.s4_covoitmobile.data.EAccountStatus;
import fr.greta.cda.s4_covoitmobile.models.User;
import fr.greta.cda.s4_covoitmobile.security.annotations.IsModerator;
import fr.greta.cda.s4_covoitmobile.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController
{
	private final UserService userService;
	
	@Autowired
	public UserController(final UserService userService) {this.userService = userService;}
	
	@PostMapping("/create")
	@ResponseStatus(HttpStatus.CREATED)
	public User createUser(@RequestBody User user)
	{
		return userService.registerNewUser(
			user.getLogin(),
			user.getPassword(),
			List.of(EAccountRole.ROLE_USER),
			EAccountStatus.PENDING);
	}
	
	@GetMapping("/me")
	@IsModerator
	public ResponseEntity<String> testAuth()
	{
		return ResponseEntity.ok("Si tu vois ce message, c'est que ton JWT est valide et que tu as le bon rôle !");
	}
}
