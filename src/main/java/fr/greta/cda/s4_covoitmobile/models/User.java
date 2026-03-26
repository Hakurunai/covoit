package fr.greta.cda.s4_covoitmobile.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "app_user")
@Data
@EqualsAndHashCode(callSuper = true)
public class User extends TimeStampedEntity
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(unique = true, nullable = false, length = 255)
	@NotBlank
	@Email
	private String email;
	
	@Column(nullable = false, length = 60)
	@NotBlank
	private String password;
	
	@OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	@PrimaryKeyJoinColumn
	@EqualsAndHashCode.Exclude
	private UserProfile userProfile;
	
	@ManyToOne
	@JoinColumn(name = "account_status_id")
	private AccountStatus accountStatus;
	
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(
		name = "user_account_role",
		joinColumns = @JoinColumn(name = "user_id"),
		inverseJoinColumns = @JoinColumn(name = "role_id")
	)
	private Set<AccountRole> roles = new HashSet<>();
	
	@OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	private RefreshToken refreshToken;
	
	@OneToMany(mappedBy = "passenger")
	private List<PassengerReservation> myReservations = new ArrayList<>();
}
