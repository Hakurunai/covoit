package fr.greta.cda.s4_covoitmobile.repositories;

import fr.greta.cda.s4_covoitmobile.models.CarBrand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarBrandRepository extends JpaRepository<CarBrand, Long>
{
	void deleteByName(String name);
	
	boolean existsByName(String name);
}
