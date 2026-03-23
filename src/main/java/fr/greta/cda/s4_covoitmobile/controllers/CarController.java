package fr.greta.cda.s4_covoitmobile.controllers;

import fr.greta.cda.s4_covoitmobile.dto.car.*;
import fr.greta.cda.s4_covoitmobile.models.Car;
import fr.greta.cda.s4_covoitmobile.security.annotations.IsUser;
import fr.greta.cda.s4_covoitmobile.services.CarService;
import fr.greta.cda.s4_covoitmobile.services.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cars")
public class CarController
{
	private final CarService carService;
	
	@PostMapping
	@IsUser
	public ResponseEntity<CreateCarResponse> createCar(
		@Valid @RequestBody CreateCarRequest request)
	{
		Car newCar = carService.createCar(request.getBrandId(),
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
	
	@GetMapping
	@IsUser
	public ResponseEntity<GetCarResponse> getCarData()
	{
		final Long userId = SecurityUtils.getAuthenticatedUser().getId();
		Optional<Car> carResult = carService.getCarFromUserProfile(userId);
		
		if (carResult.isEmpty())
		{return ResponseEntity.noContent().build();}
		
		Car car = carResult.get();
		GetCarResponse response = new GetCarResponse(
			car.getId(),
			car.getBrand().getName(),
			car.getModel(),
			car.getNbSeats(),
			car.getLicensePlate());
		
		return ResponseEntity.status(HttpStatus.FOUND).body(response);
	}
	
	@GetMapping("/{id}")
	@IsUser
	public ResponseEntity<GetCarByIdResponse> getCarData(@PathVariable Long id)
	{
		Optional<Car> carResult = carService.getCarData(id);
		
		if (carResult.isEmpty())
		{return ResponseEntity.noContent().build();}
		
		Car car = carResult.get();
		GetCarByIdResponse response = new GetCarByIdResponse(
			car.getBrand().getName(),
			car.getModel(),
			car.getNbSeats(),
			car.getLicensePlate());
		
		return ResponseEntity.status(HttpStatus.FOUND).body(response);
	}
	
	@PutMapping("/{id}")
	@IsUser
	public ResponseEntity<UpdateCarResponse> updateCarData(
		@PathVariable Long id,
		@Valid @RequestBody UpdateCarRequest request)
	{
		Car updatedCar = carService.updateCar(
			id,
			request.getBrandId(), request.getModel(),
			request.getLicensePlate(), request.getSeats());
		
		var response = new UpdateCarResponse(
			updatedCar.getBrand().getId(),
			updatedCar.getModel(),
			updatedCar.getNbSeats(),
			updatedCar.getLicensePlate());
		
		return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
	}
	
	
	@DeleteMapping("/{id}")
	@IsUser
	public ResponseEntity<Void> deleteVehicle(@PathVariable Long id)
	{
		carService.deleteCarById(id);
		
		return ResponseEntity.accepted().build();
	}
}
