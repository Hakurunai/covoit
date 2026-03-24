package fr.greta.cda.s4_covoitmobile.services;

import fr.greta.cda.s4_covoitmobile.dto.trip.CreateTripRequest;
import fr.greta.cda.s4_covoitmobile.exceptions.RideAvailablePlaceInvalidException;
import fr.greta.cda.s4_covoitmobile.models.Car;
import fr.greta.cda.s4_covoitmobile.models.City;
import fr.greta.cda.s4_covoitmobile.models.Ride;
import fr.greta.cda.s4_covoitmobile.models.User;
import fr.greta.cda.s4_covoitmobile.repositories.PassengerReservationRepository;
import fr.greta.cda.s4_covoitmobile.repositories.RideRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class RideService
{
	private final RideRepository rideRepository;
	private final PassengerReservationRepository reservationRepository;
	private final CityService cityService;
	
	private final UserService userService;
	private final CarService carService;
	
	
	@Transactional
	public Ride createRide(CreateTripRequest requestData)
	{
		SecurityUtils.checkOwnership(requestData.getDriverId());
		
		User driver = userService.getUserDetail(requestData.getDriverId());
		
		checkVehicleCompatibilityWithRequestedRide(driver, requestData.getAvailablePlaces());
		
		City departCity = cityService.getOrCreate(requestData.getDepartureCityName(), requestData.getDepartureZip());
		City arrivalCity = cityService.getOrCreate(requestData.getArrivalCityName(), requestData.getArrivalZip());
		
		Ride newRide = Ride.builder()
			.driver(driver)
			.distanceKm(requestData.getKms())
			.departDate(requestData.getDepartureDate())
			.availablePlace(requestData.getAvailablePlaces())
			.departureCity(departCity)
			.arrivalCity(arrivalCity)
			.build();
		
		return rideRepository.save(newRide);
	}
	
	@Transactional
	public void deleteAllRidesFromUser(Long driverId)
	{
		rideRepository.deleteByDriver_Id(driverId);
	}
	
	private void checkVehicleCompatibilityWithRequestedRide(final User driver, final short ridePlace)
	{
		//if the user did not have a Car, carService will throw a ResourceNotFoundException
		Car driverCar = carService.getCarFromUserProfile(driver.getId());
		
		if (driverCar.getNbSeats() < ridePlace)
		{
			throw new RideAvailablePlaceInvalidException(driver.getId(), ridePlace, driverCar.getNbSeats());
		}
	}
//	@Transactional
//	public PassengerReservation reservePlace(Long rideId)
//	{
//		Ride ride = rideRepository.findById(rideId)
//			.orElseThrow(() -> new ResourceNotFoundException("Ride", "id", rideId));
//
//		Long currentUserId = SecurityUtils.getAuthenticatedUser().getId();
//
//		// 1. Vérifications métier
//		if (ride.getDriver().getId().equals(currentUserId)) {
//			throw new IllegalStateException("Le conducteur ne peut pas réserver son propre trajet");
//		}
//
//		if (ride.getAvailablePlace() <= 0) {
//			throw new IllegalStateException("Plus de places disponibles");
//		}
//
//		if (reservationRepository.existsByRideIdAndIdUserIdAndIsCancelledFalse(rideId, currentUserId))
//		{
//			throw new AlreadyExistException("Vous avez déjà une réservation active pour ce trajet");
//		}
//
//		// 2. Création de la réservation
//		PassengerReservation res = new PassengerReservation();
//		res.setRide(ride);
//		res.setIdUser(userService.getUserById(currentUserId));
//
//		// 3. Mise à jour du trajet (Décrémentation)
//		ride.setAvailablePlace((byte) (ride.getAvailablePlace() - 1));
//
//		return reservationRepository.save(res);
//	}
//
//	@Transactional
//	public void cancelReservation(Long reservationId)
//	{
//		PassengerReservation res = reservationRepository.findById(reservationId)
//			.orElseThrow(() -> new ResourceNotFoundException("Reservation", "id", reservationId));
//
//		// Sécurité : Seul le passager concerné peut annuler sa propre réservation
//		SecurityUtils.checkOwnership(res.getIdUser().getId());
//
//		if (!res.isCancelled()) {
//			res.setCancelled(true);
//			// On rend la place au trajet
//			Ride ride = res.getRide();
//			ride.setAvailablePlace((byte) (ride.getAvailablePlace() + 1));
//
//			reservationRepository.save(res);
//		}
//	}
}
