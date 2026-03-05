package fr.greta.cda.s4_covoitmobile.services;

import fr.greta.cda.s4_covoitmobile.exceptions.AlreadyExistException;
import fr.greta.cda.s4_covoitmobile.exceptions.ResourceNotFoundException;
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
	
	@Transactional
	public CarBrand updateBrand(final Long id, String brandName)
	{
		CarBrand brand = carBrandRepository.findById(id)
			.orElseThrow(() -> new ResourceNotFoundException("Brand", "Id", id));
		
		if (carBrandRepository.existsByName(brandName))
		{
			throw new AlreadyExistException("Brand name " + brandName + " is already taken");
		}
		
		brand.setName(brandName);
		return brand;
	}
	
	@Transactional
	public void deleteBrand(final Long id)
	{
		CarBrand brand = carBrandRepository.findById(id)
			.orElseThrow(() -> new ResourceNotFoundException("Brand", "Id", id));
		
		carBrandRepository.delete(brand);
	}
	
	/**
	 * If the brand did not exist, no exception will be thrown
	 *
	 * @param brandName the targeted brand to delete
	 */
	@Transactional
	public void silentlyDeleteCarBrand(final String brandName)
	{
		carBrandRepository.deleteByName(brandName);
	}
	
	public boolean isEmpty()
	{
		return carBrandRepository.count() == 0;
	}
	
	public void saveAll(List<CarBrand> brandList)
	{
		carBrandRepository.saveAll(brandList);
	}
}
