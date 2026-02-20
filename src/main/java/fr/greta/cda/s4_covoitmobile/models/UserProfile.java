package fr.greta.cda.s4_covoitmobile.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class UserProfile
{
	@Id
	private Long id;
	
	private String firstname;
	private String lastname;
	private String phone;
	
	@OneToOne
	@MapsId //we use the id attribute of a User entity as our own id
	@JoinColumn(name = "id")
	private User user;
}