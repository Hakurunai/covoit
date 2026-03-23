package fr.greta.cda.s4_covoitmobile.dto.trip;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateTripRequest
{
	@NotNull(message = "A driver is required")
	Long driverId;
	
	@NotNull(message = "A distance is required")
	@Digits(integer = 5, fraction = 1)
	@DecimalMin(value = "1.0", inclusive = true, message = "Distance must be at least 1 km")
	@DecimalMax(value = "40000.0", inclusive = true, message = "Distance can not be higher than 40 000 km")
	Float kms;
	
	@NotNull(message = "Departure date is required")
	@Future(message = "Departure date must be in the future")
	private LocalDateTime departureDate;
	
	@Min(value = 1, message = "At least 1 available seat required")
	@Max(value = 15, message = "Maximum 15 available seats allowed")
	private short availablePlaces;
	
	@NotBlank(message = "Departure city name is required")
	private String departureCityName;
	
	@NotBlank(message = "Departure postal code is required")
	private String departureZip;
	
	@NotBlank(message = "Arrival city name is required")
	private String arrivalCityName;
	
	@NotBlank(message = "Arrival postal code is required")
	private String arrivalZip;
}
