package fr.greta.cda.s4_covoitmobile.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreateUserRequest
{
	@NotBlank(message = "email is required")
	@Email(message = "Email format invalid")
	private String email;
	
	@NotBlank(message = "password is required")
	@Size(min = 8, max = 64, message = "Password length must be comprise between 8 and 64 characters")
	private String password;
}
