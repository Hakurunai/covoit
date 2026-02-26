package fr.greta.cda.s4_covoitmobile.dto.userprofile;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UpdateProfileRequest
{
	@Size(min = 2, max = 50, message = "firstname length must be between 2 and 50 characters")
	private String firstname;
	
	@Size(min = 2, max = 50, message = "lastname length must be between 2 and 50 characters")
	private String lastname;
	
	@Pattern(regexp = "^\\+?[0-9\\s.\\-\\(\\)]{7,20}$",
		message = "Invalid phone number format")
	private String phone;
}
