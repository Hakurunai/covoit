package fr.greta.cda.s4_covoitmobile.controllers;

import fr.greta.cda.s4_covoitmobile.dto.trip.book.BookAPlaceOnRideRequest;
import fr.greta.cda.s4_covoitmobile.security.annotations.IsUser;
import fr.greta.cda.s4_covoitmobile.services.RideService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/trips/booking")
public class ReservationBookController
{
	private final RideService rideService;
	
	@PostMapping("/{tripId}")
	@IsUser
	public ResponseEntity<Void> bookAPlaceOnTrip(
		@PathVariable Long tripId,
		@Valid @RequestBody BookAPlaceOnRideRequest request)
	{
		rideService.bookAPlaceOnRide(tripId, request);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}
}
