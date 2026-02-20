package fr.greta.cda.s4_covoitmobile.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TokenRefreshResponse
{
	private String token;
}
