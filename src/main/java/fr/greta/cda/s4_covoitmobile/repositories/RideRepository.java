package fr.greta.cda.s4_covoitmobile.repositories;

import fr.greta.cda.s4_covoitmobile.models.Ride;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RideRepository extends JpaRepository<Ride, Long>
{
	Optional<Ride> findByIdAndIsCancelledFalse(Long id);
	
	void deleteByDriver_Id(Long driverId);
}
