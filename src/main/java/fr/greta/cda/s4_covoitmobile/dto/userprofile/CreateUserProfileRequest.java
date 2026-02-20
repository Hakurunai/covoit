package fr.greta.cda.s4_covoitmobile.dto.userprofile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreateUserProfileRequest
{
	@NotBlank(message = "firstname is required")
	private String firstname;
	
	@NotBlank(message = "lastname is required")
	private String lastname;
	
	@NotBlank(message = "phone is required")
	@Pattern(regexp = "^\\+?[0-9\\s.\\-\\(\\)]{7,20}$",
		message = "Invalid phone number format")
	private String phone;
}
