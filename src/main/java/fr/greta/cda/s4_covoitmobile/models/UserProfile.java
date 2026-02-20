package fr.greta.cda.s4_covoitmobile.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class UserProfile extends TimeStampedEntity
{
	@Id
	private Long id;
	
	@Column(nullable = false, length = 50)
	@NotBlank
	private String firstname;
	
	@Column(nullable = false, length = 50)
	@NotBlank
	private String lastname;
	
	@Column(nullable = false, length = 20)
	@NotBlank
	private String phone;
	
	@OneToOne
	@MapsId //we use the id attribute of a User entity as our own id
	@JoinColumn(name = "id")
	private User user;
}