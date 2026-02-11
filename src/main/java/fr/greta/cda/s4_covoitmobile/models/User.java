package fr.greta.cda.s4_covoitmobile.models;

import jakarta.persistence.*;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "app_user")
@Data
public class User
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(unique = true, nullable = false, length = 100)
	private String login;
	
	@Column(nullable = false)
	private String password;
	
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
}
