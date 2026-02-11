package fr.greta.cda.s4_covoitmobile.controllers;

import fr.greta.cda.s4_covoitmobile.models.User;
import fr.greta.cda.s4_covoitmobile.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController
{
	private final UserService userService;
	
	@Autowired
	public UserController(final UserService userService) {this.userService = userService;}
	
	@PostMapping("/create")
	@ResponseStatus(HttpStatus.CREATED)
	public User CreateUser(@RequestBody User user)
	{
		return userService.registerNewUser(user.getLogin(), user.getPassword());
	}
}
