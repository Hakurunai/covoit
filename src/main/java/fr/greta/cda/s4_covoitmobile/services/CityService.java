package fr.greta.cda.s4_covoitmobile.services;

import fr.greta.cda.s4_covoitmobile.exceptions.AlreadyExistException;
import fr.greta.cda.s4_covoitmobile.exceptions.ResourceNotFoundException;
import fr.greta.cda.s4_covoitmobile.models.City;
import fr.greta.cda.s4_covoitmobile.repositories.CityRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class CityService
{
	private final CityRepository cityRepository;
	
	public City getCityById(Long id)
	{
		return cityRepository.findById(id)
			.orElseThrow(() -> new ResourceNotFoundException("City", "Id", id));
	}
	
	public City getOrCreate(String targetName, String zipCode)
	{
		return cityRepository.findByName(targetName)
			.orElseGet(() ->
			{
				City newCity = new City(targetName, zipCode);
				return cityRepository.save(newCity);
			});
	}
	
	@Transactional
	public City registerCity(String name, String postalCode)
	{
		if (cityRepository.existsByNameAndPostalCode(name, postalCode))
		{
			throw new AlreadyExistException("This city is already registered");
		}
		
		return cityRepository.save(new City(name, postalCode));
	}
}
