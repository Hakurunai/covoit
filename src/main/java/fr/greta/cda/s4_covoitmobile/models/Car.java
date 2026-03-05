package fr.greta.cda.s4_covoitmobile.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Car
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne(optional = false)
	@JoinColumn(name = "car_brand_id", nullable = false)
	private CarBrand brand;
	
	@OneToOne(optional = false)
	@JoinColumn(name = "user_profile_id", nullable = false, unique = true)
	private UserProfile userProfile;
	
	@Column(nullable = false, unique = true, length = 15)
	@NotBlank
	@Size(max = 15)
	private String licensePlate;
	
	@Column(nullable = false)
	@Min(1)
	@Max(15)
	private short nbSeats;
	
	@Column(nullable = false, unique = true, length = 20)
	@NotBlank
	@Size(max = 20)
	private String model;
}
