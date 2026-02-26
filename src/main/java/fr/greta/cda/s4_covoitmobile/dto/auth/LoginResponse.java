package fr.greta.cda.s4_covoitmobile.dto.auth;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class LoginResponse
{
	private String id;
	private String token;
	private String refreshToken;
	private List<String> roles;
	private String accountStatus;
	private String type;
	@JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
	private LocalDateTime createdAt;
	
	public LoginResponse(Long pId, String pToken, String pRefreshToken, List<String> pRoles, String pAccountStatus,
		LocalDateTime pCreatedAt)
	{
		id = pId.toString();
		type = "Bearer";
		token = pToken;
		refreshToken = pRefreshToken;
		roles = pRoles;
		accountStatus = pAccountStatus;
		createdAt = pCreatedAt;
	}
}
