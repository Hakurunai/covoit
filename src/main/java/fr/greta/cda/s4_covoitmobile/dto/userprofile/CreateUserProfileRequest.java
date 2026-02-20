package fr.greta.cda.s4_covoitmobile.dto.userprofile;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateUserProfileRequest
{
	@NotBlank
	private String firstname;
	
	@NotBlank
	private String lastname;
	
	@NotBlank
	private String phone;
}
