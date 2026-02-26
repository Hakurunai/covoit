package fr.greta.cda.s4_covoitmobile.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.Instant;

@Entity(name = "refreshToken")
@Data
@EqualsAndHashCode(exclude = "user")
public class RefreshToken {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@OneToOne
	@JoinColumn(name = "user_id", referencedColumnName = "id")
	@ToString.Exclude
	private User user;
	
	@Column(nullable = false, unique = true, length = 64)
	@NotBlank
	private String token;
	
	@Column(nullable = false)
	@NotNull
	private Instant expiryDate;
}
