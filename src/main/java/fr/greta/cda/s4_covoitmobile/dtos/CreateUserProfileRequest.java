package fr.greta.cda.s4_covoitmobile.dtos;

import lombok.Data;

@Data
public class CreateUserProfileRequest
{
	private String firstname;
	private String lastname;
	private String phone;
}
