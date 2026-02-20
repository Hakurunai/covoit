package fr.greta.cda.s4_covoitmobile.models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;

@Entity(name = "refreshToken")
@Data
public class RefreshToken {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@OneToOne
	@JoinColumn(name = "user_id", referencedColumnName = "id")
	private User user;
	
	@Column(nullable = false, unique = true, length = 64)
	private String token;
	
	@Column(nullable = false)
	private Instant expiryDate;
}
