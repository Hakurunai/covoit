package fr.greta.cda.s4_covoitmobile.repositories;

import fr.greta.cda.s4_covoitmobile.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>
{
	Optional<User> findByEmail(String email);
	
	boolean existsByEmail(String email);
	
	@Query("SELECT DISTINCT u FROM User u " +
		   "LEFT JOIN FETCH u.userProfile " +
		   "LEFT JOIN FETCH u.accountStatus " +
		   "LEFT JOIN FETCH u.roles " +
		   "LEFT JOIN FETCH u.refreshToken")
	List<User> findAllWithProfileAndStatusAndRoles();
	
	
	@Query("SELECT u FROM User u " +
		   "LEFT JOIN FETCH u.userProfile " +
		   "LEFT JOIN FETCH u.accountStatus " +
		   "LEFT JOIN FETCH u.refreshToken " +
		   "WHERE u.id = :id")
	Optional<User> findByIdWithAllRelations(@Param("id") Long id);
}
