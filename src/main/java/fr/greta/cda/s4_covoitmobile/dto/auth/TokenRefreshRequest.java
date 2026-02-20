package fr.greta.cda.s4_covoitmobile.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TokenRefreshRequest
{
	@NotBlank
	private String refreshToken;
}
