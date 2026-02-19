package fr.greta.cda.s4_covoitmobile.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class LoginResponse
{
	private String token;
	private String refreshToken;
	private String type;
	private String username;
	private List<String> roles;
	
	public LoginResponse(String pToken, String pRefreshToken, String pUsername, List<String> pRoles)
	{
		token = pToken;
		refreshToken = pRefreshToken;
		type = "Bearer";
		username = pUsername;
		roles = pRoles;
	}
}
