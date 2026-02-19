package fr.greta.cda.s4_covoitmobile.dtos.auth;

import lombok.Data;

import java.util.List;

@Data
public class LoginResponse
{
	private String token;
	private String refreshToken;
	private List<String> roles;
	private String type;
	
	public LoginResponse(String pToken, String pRefreshToken, List<String> pRoles)
	{
		token = pToken;
		refreshToken = pRefreshToken;
		type = "Bearer";
		roles = pRoles;
	}
}
