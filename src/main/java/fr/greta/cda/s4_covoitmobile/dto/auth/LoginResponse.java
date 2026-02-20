package fr.greta.cda.s4_covoitmobile.dto.auth;

import lombok.Data;

import java.util.List;

@Data
public class LoginResponse
{
	private String token;
	private String refreshToken;
	private List<String> roles;
	private String accountStatus;
	private String type;
	
	public LoginResponse(String pToken, String pRefreshToken, List<String> pRoles, String pAccountStatus)
	{
		type = "Bearer";
		token = pToken;
		refreshToken = pRefreshToken;
		roles = pRoles;
		accountStatus = pAccountStatus;
	}
}
