package fr.greta.cda.s4_covoitmobile.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateUserRequest
{
	@NotBlank(message = "email is required")
	@Email(message = "Email format invalid")
	private String email;
	
	@NotBlank(message = "password is required")
	@Size(min = 8, max = 64)
	private String password;
}
