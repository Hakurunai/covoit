package fr.greta.cda.s4_covoitmobile.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class JwtResponse
{
	public JwtResponse(String pToken, String pUsername, List<String> pRoles)
	{
		token = pToken;
		type = "Bearer";
		username = pUsername;
		roles = pRoles;
	}
	
	private String token;
	private String type;
	private String username;
	private List<String> roles;
}
