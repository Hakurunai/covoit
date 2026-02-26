package fr.greta.cda.s4_covoitmobile.dto.userprofile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateUserProfileResponse
{
	private String firstName;
	private String lastName;
	private String phone;
	private List<String> roles;
	private String accountStatus;
}
