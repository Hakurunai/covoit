package fr.greta.cda.s4_covoitmobile.controllers;

import fr.greta.cda.s4_covoitmobile.dto.trip.CreateTripRequest;
import fr.greta.cda.s4_covoitmobile.dto.trip.CreateTripResponse;
import fr.greta.cda.s4_covoitmobile.dto.trip.GetTripDetailResponse;
import fr.greta.cda.s4_covoitmobile.models.Ride;
import fr.greta.cda.s4_covoitmobile.security.annotations.IsUser;
import fr.greta.cda.s4_covoitmobile.services.RideService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/trips")
public class RideController
{
	private final RideService rideService;
	
	@PostMapping
	@IsUser
	public ResponseEntity<CreateTripResponse> createTrip(@Valid @RequestBody CreateTripRequest request)
	{
		Ride ride = rideService.createRide(request);
		
		CreateTripResponse response = CreateTripResponse.builder()
			.tripId(ride.getId())
			.driverId(ride.getDriver().getId())
			.kms(ride.getDistanceKm())
			.departDate(ride.getDepartDate())
			.availablePlaces(ride.getAvailablePlace())
			.departureCityName(ride.getDepartureCity().getName())
			.departureZip(ride.getDepartureCity().getPostalCode())
			.arrivalCityName(ride.getArrivalCity().getName())
			.arrivalZip(ride.getArrivalCity().getPostalCode())
			.build();
		
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}
	
	@GetMapping("/{tripId}")
	@IsUser
	public ResponseEntity<GetTripDetailResponse> getTripDetail(@PathVariable Long tripId)
	{
		return ResponseEntity
			.status(HttpStatus.FOUND)
			.body(rideService.getTripDetail(tripId));
	}
	
	@DeleteMapping("/{tripId}")
	@IsUser
	public ResponseEntity<Void> deleteTrip(@PathVariable Long tripId)
	{
		rideService.cancelRide(tripId);
		
		return ResponseEntity.status(HttpStatus.ACCEPTED).build();
	}
}
