package fr.greta.cda.s4_covoitmobile.dtos;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UserResponseDto
{
	private Long id;
	private String login;
	private List<String> roles;
	private String status;
}
