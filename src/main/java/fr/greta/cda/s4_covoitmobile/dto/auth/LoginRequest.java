package fr.greta.cda.s4_covoitmobile.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data // @Data did not insert @AllArgsCtor, but we do not need one, cause Jackson won't use it
public class LoginRequest
{
	@NotBlank(message = "email is required")
	@Email(message = "Email format invalid")
	private String email;
	
	@NotBlank(message = "password is required")
	private String password;
}
