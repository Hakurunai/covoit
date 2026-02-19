package fr.greta.cda.s4_covoitmobile.dtos;

import lombok.Data;

@Data // @Data did not insert @AllArgsCtor, but we do not need one, cause Jackson won't use it
public class LoginRequest
{
	private String login;
	private String password;
}
