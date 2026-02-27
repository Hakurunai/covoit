package fr.greta.cda.s4_covoitmobile.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "car_brand")
@Getter
@Setter
@NoArgsConstructor
public class CarBrand
{
	public CarBrand(String brandName)
	{
		name = brandName;
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(unique = true, nullable = false, length = 40)
	@NotBlank
	@Size(min = 2, max = 40, message = "A brand name must me comprise between 2 and 40 characters")
	private String name;
}
