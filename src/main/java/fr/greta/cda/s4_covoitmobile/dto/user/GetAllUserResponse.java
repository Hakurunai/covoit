package fr.greta.cda.s4_covoitmobile.dto.user;

import fr.greta.cda.s4_covoitmobile.models.User;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GetAllUserResponse
{
	private Long id;
	private String email;
	private String firstname;
	private String lastname;
	private String phone;
	private String status;
	private String refreshToken;
	private String tokenExpiration;
	
	public GetAllUserResponse(User user) {
		this(
			user.getId(),
			user.getEmail(),
			user.getUserProfile() != null ? user.getUserProfile().getFirstname() : null,
			user.getUserProfile() != null ? user.getUserProfile().getLastname() : null,
			user.getUserProfile() != null ? user.getUserProfile().getPhone() : null,
			user.getAccountStatus() != null ? user.getAccountStatus().getName().name() : "UNKNOWN",
			user.getRefreshToken() != null ? user.getRefreshToken().getToken() : "NO_TOKEN",
			user.getRefreshToken() != null ? user.getRefreshToken().getExpiryDate().toString() : "N/A"
		);
	}
}
