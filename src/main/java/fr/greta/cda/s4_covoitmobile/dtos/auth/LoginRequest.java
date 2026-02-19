package fr.greta.cda.s4_covoitmobile.dtos.auth;

import lombok.Data;

@Data // @Data did not insert @AllArgsCtor, but we do not need one, cause Jackson won't use it
public class LoginRequest
{
	private String email;
	private String password;
}
