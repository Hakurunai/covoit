package fr.greta.cda.s4_covoitmobile.dtos.auth;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TokenRefreshRequest
{
	private String refreshToken;
}
