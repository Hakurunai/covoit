package fr.greta.cda.s4_covoitmobile.dto.car;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateCarRequest
{
	@NotNull(message = "A brand id is required")
	Long brandId;
	
	@NotBlank(message = "A model is required")
	@Size(min = 2, max = 20, message = "model name must be comprise between 2 and 20 characters")
	private String model;
	
	@NotNull
	@Min(value = 1, message = "A car need at least one seat")
	@Max(value = 15, message = "A car has a maximum of 15 seats")
	private Short seats;
	
	@NotBlank
	@Size(min = 8, max = 15, message = "A licence plate must be comprise between 8 and 15 characters")
	private String licensePlate;
}
