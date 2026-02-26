package fr.greta.cda.s4_covoitmobile.dto.userprofile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateUserProfileResponse
{
	private String firstName;
	private String lastName;
	private String phone;
	private String accountStatus;
}
