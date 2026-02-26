package fr.greta.cda.s4_covoitmobile.dto.user;

import fr.greta.cda.s4_covoitmobile.models.User;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GetPersonByIdResponse
{
	private String id;
	private String email;
	private String firstname;
	private String lastname;
	private String phone;
	
	public GetPersonByIdResponse(User user)
	{
		this(
			user.getId().toString(),
			user.getEmail(),
			user.getUserProfile() != null ? user.getUserProfile().getFirstname() : null,
			user.getUserProfile() != null ? user.getUserProfile().getLastname() : null,
			user.getUserProfile() != null ? user.getUserProfile().getPhone() : null
		);
	}
}
