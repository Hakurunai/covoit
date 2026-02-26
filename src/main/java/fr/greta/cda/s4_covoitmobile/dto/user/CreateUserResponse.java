package fr.greta.cda.s4_covoitmobile.dto.user;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CreateUserResponse
{
	private Long id;
	private String email;
	private List<String> roles;
	private String status;
}
