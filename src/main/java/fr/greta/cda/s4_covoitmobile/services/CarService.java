package fr.greta.cda.s4_covoitmobile.services;

import fr.greta.cda.s4_covoitmobile.exceptions.AlreadyExistException;
import fr.greta.cda.s4_covoitmobile.exceptions.ResourceNotFoundException;
import fr.greta.cda.s4_covoitmobile.models.Car;
import fr.greta.cda.s4_covoitmobile.models.CarBrand;
import fr.greta.cda.s4_covoitmobile.models.UserProfile;
import fr.greta.cda.s4_covoitmobile.repositories.CarBrandRepository;
import fr.greta.cda.s4_covoitmobile.repositories.CarRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class CarService
{
	private final CarRepository carRepository;
	private final CarBrandRepository carBrandRepository;
	private final UserService userService;
	
	@Transactional
	public Car createCar(Long carBrandId, String model, String plate, short nbSeats)
	{
		Long userId = SecurityUtils.getAuthenticatedUser().getId();
		return saveCarInternal(carBrandId, userId, model, plate, nbSeats);
	}
	
	@Transactional
	public Car saveCarInternal(Long carBrandId, Long userId, String model, String plate, short nbSeats)
	{
		if (carRepository.existsByLicensePlate(plate))
		{throw new AlreadyExistException("The license plate " + plate + " is already registered");}
		
		if (carRepository.existsByUserProfileId(userId))
		{throw new AlreadyExistException("User " + userId + " already has a car registered");}
		
		UserProfile profile = userService.getProfile(userId);
		CarBrand carBrand = this.getBrand(carBrandId);
		
		Car car = new Car();
		car.setModel(model);
		car.setLicensePlate(plate);
		car.setNbSeats(nbSeats);
		car.setBrand(carBrand);
		car.setUserProfile(profile);
		profile.setCar(car);
		
		return carRepository.save(car);
	}
	
	public Car getCarFromUserProfile(Long userProfileId)
	{
		return getCarFromUserProfileInternal(userProfileId).orElseThrow
			(() -> new ResourceNotFoundException("Car", "from UserId", userProfileId));
	}
	
	public Optional<Car> getCarFromUserProfileInternal(Long userProfileId)
	{
		return carRepository.findByUserProfileId(userProfileId);
	}
	
	public Optional<Car> getCarData(Long carId)
	{
		return carRepository.findById(carId);
	}
	
	
	@Transactional
	public Car updateCar(Long carId, Long carBrandId, String model, String plate, short nbSeats)
	{
		Car car = carRepository.findById(carId)
			.orElseThrow(() -> new ResourceNotFoundException("Car", "Id", carId));
		
		if (!car.getLicensePlate().equals(plate) && carRepository.existsByLicensePlate(plate))
		{
			throw new AlreadyExistException("The license plate " + plate + " is already registered by another user");
		}
		
		SecurityUtils.checkOwnership(car.getUserProfile().getId());
		
		CarBrand brand = getBrand(carBrandId);
		
		car.setModel(model);
		car.setLicensePlate(plate);
		car.setNbSeats(nbSeats);
		car.setBrand(brand);
		
		return carRepository.save(car);
	}
	
	@Transactional
	public void deleteCarById(final Long id)
	{
		Optional<Car> targetedCar = getCarData(id);
		if (targetedCar.isEmpty())
		{
			throw new ResourceNotFoundException("Car", "Id", id);
		}
		Car car = targetedCar.get();
		UserProfile ownerProfile = car.getUserProfile();
		if (ownerProfile != null)
		{
			SecurityUtils.checkOwnership(ownerProfile.getId());
			ownerProfile.setCar(null);
		}
		
		carRepository.delete(car);
	}
	
	@Transactional
	public CarBrand createNewBrand(String name)
	{
		if (carBrandRepository.existsByName(name))
		{
			throw new AlreadyExistException("Car brand named " + name + " already exist");
		}
		
		return carBrandRepository.save(new CarBrand(name));
	}
	
	public CarBrand getBrand(Long targetedId)
	{
		return carBrandRepository.findById(targetedId)
			.orElseThrow(() -> new ResourceNotFoundException("Car Brand", "Id", targetedId));
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
