package fr.greta.cda.s4_covoitmobile.dto.trip;

import fr.greta.cda.s4_covoitmobile.dto.user.DetailedUserContactResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetTripDetailResponse
{
	private DetailedUserContactResponse driverContact;
	
	@Builder.Default
	private List<DetailedUserContactResponse> passengerContacts = new ArrayList<>();
	
	private Float kms;
	
	private LocalDateTime departDate;
	
	private short availablePlaces;
	
	private String departureCityName;
	
	private String departureZip;
	
	private String arrivalCityName;
	
	private String arrivalZip;
}
