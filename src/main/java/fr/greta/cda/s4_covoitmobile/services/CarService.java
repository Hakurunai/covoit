package fr.greta.cda.s4_covoitmobile.services;

import fr.greta.cda.s4_covoitmobile.exceptions.AlreadyExistException;
import fr.greta.cda.s4_covoitmobile.models.CarBrand;
import fr.greta.cda.s4_covoitmobile.repositories.CarBrandRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CarService
{
	private final CarBrandRepository carBrandRepository;
	
	@Transactional
	public CarBrand createNewBrand(String name)
	{
		if (carBrandRepository.existsByName(name))
		{
			throw new AlreadyExistException("Car brand named " + name + " already exist");
		}
		
		return carBrandRepository.save(new CarBrand(name));
	}
	
	public List<CarBrand> getAllBrand()
	{
		return carBrandRepository.findAll();
	}
}
