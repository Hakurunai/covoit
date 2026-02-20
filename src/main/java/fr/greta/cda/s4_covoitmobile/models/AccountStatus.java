package fr.greta.cda.s4_covoitmobile.models;

import fr.greta.cda.s4_covoitmobile.data.EAccountStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class AccountStatus
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Enumerated(EnumType.STRING)
	@Column(length = 20, nullable = false, unique = true)
	@NotNull
	private EAccountStatus name;
}
