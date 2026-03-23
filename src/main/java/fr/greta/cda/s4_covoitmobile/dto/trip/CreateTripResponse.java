package fr.greta.cda.s4_covoitmobile.dto.trip;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateTripResponse
{
	private Long tripId;
	
	private Long driverId;
	
	private Float kms;
	
	private LocalDateTime departDate;
	
	private String model;
	
	private short availablePlaces;
	
	private String departureCityName;
	
	private String departureZip;
	
	private String arrivalCityName;
	
	private String arrivalZip;
}
