package fr.greta.cda.s4_covoitmobile.controllers;

import fr.greta.cda.s4_covoitmobile.dto.car.CreateCarRequest;
import fr.greta.cda.s4_covoitmobile.dto.car.CreateCarResponse;
import fr.greta.cda.s4_covoitmobile.models.Car;
import fr.greta.cda.s4_covoitmobile.security.UserDetailsImpl;
import fr.greta.cda.s4_covoitmobile.security.annotations.IsUser;
import fr.greta.cda.s4_covoitmobile.services.CarService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cars")
public class CarController
{
	private final CarService carService;
	
	@PostMapping
	@IsUser
	public ResponseEntity<CreateCarResponse> createCar(@Valid @RequestBody CreateCarRequest request,
		@AuthenticationPrincipal UserDetails userDetails)
	{
		final Long userId = ((UserDetailsImpl) userDetails).getId();
		Car newCar = carService.createCar(request.getBrandId(), userId,
			request.getModel(), request.getLicensePlate(),
			request.getSeats());
		
		var response = new CreateCarResponse(
			newCar.getId(),
			newCar.getBrand().getId(),
			newCar.getModel(),
			newCar.getNbSeats(),
			newCar.getLicensePlate());
		
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}
}
