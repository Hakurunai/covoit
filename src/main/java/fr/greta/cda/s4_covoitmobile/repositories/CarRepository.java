package fr.greta.cda.s4_covoitmobile.repositories;

import fr.greta.cda.s4_covoitmobile.models.Car;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarRepository extends JpaRepository<Car, Long>
{
	boolean existsByLicensePlate(String licensePlate);
	
	boolean existsByUserProfileId(Long userProfileId);
}
