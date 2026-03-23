package fr.greta.cda.s4_covoitmobile.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(uniqueConstraints =
{
	@UniqueConstraint(columnNames = {"name", "postalCode"})
})
@Data
@NoArgsConstructor
public class City
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = false, length = 80)
	private String name;
	
	@Column(nullable = false, length = 10)
	private String postalCode;
	
	public City(String name, String postalCode)
	{
		this.name = name;
		this.postalCode = postalCode;
	}
}