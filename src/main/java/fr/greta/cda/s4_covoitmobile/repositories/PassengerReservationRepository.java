package fr.greta.cda.s4_covoitmobile.repositories;

import fr.greta.cda.s4_covoitmobile.models.PassengerReservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PassengerReservationRepository extends JpaRepository<PassengerReservation, Long>
{
}
