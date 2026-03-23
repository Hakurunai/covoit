package fr.greta.cda.s4_covoitmobile.repositories;

import fr.greta.cda.s4_covoitmobile.models.Car;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CarRepository extends JpaRepository<Car, Long>
{
	boolean existsByLicensePlate(String licensePlate);
	
	boolean existsByUserProfileId(Long userProfileId);
	
	Optional<Car> findByUserProfileId(Long userProfileId);
}
