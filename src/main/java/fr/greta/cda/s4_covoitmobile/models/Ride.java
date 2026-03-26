package fr.greta.cda.s4_covoitmobile.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Ride
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private short availablePlace;
	private float distanceKm;
	
	private LocalDateTime departDate;
	
	private boolean isCancelled = false;
	
	@ManyToOne(optional = false)
	private User driver;
	
	@ManyToOne(optional = false)
	private City departureCity;
	
	@ManyToOne(optional = false)
	private City arrivalCity;
	
	@OneToMany(mappedBy = "ride", cascade = CascadeType.ALL)
	private List<PassengerReservation> reservations = new ArrayList<>();
	
	public void addReservation(PassengerReservation reservation)
	{
		this.reservations.add(reservation);
		reservation.setRide(this);
	}
}