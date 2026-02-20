package fr.greta.cda.s4_covoitmobile.dto.userprofile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreateUserProfileRequest
{
	@NotBlank(message = "firstname is required")
	@Size(min = 2, max = 50, message = "firstname length must be between 2 and 50 characters")
	private String firstname;
	
	@NotBlank(message = "lastname is required")
	@Size(min = 2, max = 50, message = "lastname length must be between 2 and 50 characters")
	private String lastname;
	
	@NotBlank(message = "phone is required")
	@Pattern(regexp = "^\\+?[0-9\\s.\\-\\(\\)]{7,20}$",
		message = "Invalid phone number format")
	private String phone;
}
