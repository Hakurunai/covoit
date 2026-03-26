package fr.greta.cda.s4_covoitmobile.dto.user;

import fr.greta.cda.s4_covoitmobile.models.User;
import fr.greta.cda.s4_covoitmobile.models.UserProfile;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DetailedUserContactResponse
{
	public DetailedUserContactResponse(final User user)
	{
		UserProfile profile = user.getUserProfile();
		
		this.id = user.getId();
		this.firstName = profile.getFirstname();
		this.lastName = profile.getLastname();
		this.email = user.getEmail();
		this.phone = profile.getPhone();
	}
	
	private Long id;
	private String firstName;
	private String lastName;
	private String email;
	private String phone;
}
