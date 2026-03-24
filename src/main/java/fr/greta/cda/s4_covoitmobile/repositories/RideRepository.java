package fr.greta.cda.s4_covoitmobile.repositories;

import fr.greta.cda.s4_covoitmobile.models.Ride;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RideRepository extends JpaRepository<Ride, Long>
{
	void deleteByDriver_Id(Long driverId);
}
