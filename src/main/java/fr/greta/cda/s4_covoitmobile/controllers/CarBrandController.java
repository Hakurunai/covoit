package fr.greta.cda.s4_covoitmobile.controllers;

import fr.greta.cda.s4_covoitmobile.dto.brand.CreateBrandRequest;
import fr.greta.cda.s4_covoitmobile.dto.brand.CreateBrandResponse;
import fr.greta.cda.s4_covoitmobile.dto.brand.GetBrandResponse;
import fr.greta.cda.s4_covoitmobile.models.CarBrand;
import fr.greta.cda.s4_covoitmobile.security.annotations.IsAdmin;
import fr.greta.cda.s4_covoitmobile.security.annotations.IsUser;
import fr.greta.cda.s4_covoitmobile.services.CarService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/brands")
public class CarBrandController
{
	private final CarService carService;
	
	@PostMapping
	@IsAdmin
	public ResponseEntity<CreateBrandResponse> addNewBrand(@Valid @RequestBody CreateBrandRequest request)
	{
		CarBrand newBrand = carService.createNewBrand(request.getBrandName());
		
		var response = new CreateBrandResponse( newBrand.getId(), newBrand.getName());
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}
	
	@GetMapping
	@IsUser
	public ResponseEntity<List<GetBrandResponse>> getAllBrand()
	{
		List<CarBrand> allBrands = carService.getAllBrand();
		
		var response = allBrands.stream().map(GetBrandResponse::new).toList();
		return ResponseEntity.ok(response);
	}
}
