package fr.greta.cda.s4_covoitmobile.repositories;

import fr.greta.cda.s4_covoitmobile.models.City;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CityRepository extends JpaRepository<City, Long>
{
	public boolean existsByNameAndPostalCode(String name, String postalCode);
	
	public Optional<City> findByName(String name);
}
