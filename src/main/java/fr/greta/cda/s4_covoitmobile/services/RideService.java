package fr.greta.cda.s4_covoitmobile.services;

import fr.greta.cda.s4_covoitmobile.dto.trip.CreateTripRequest;
import fr.greta.cda.s4_covoitmobile.dto.trip.GetTripDetailResponse;
import fr.greta.cda.s4_covoitmobile.dto.trip.book.BookAPlaceOnRideRequest;
import fr.greta.cda.s4_covoitmobile.dto.user.DetailedUserContactResponse;
import fr.greta.cda.s4_covoitmobile.event.RideCanceledEvent;
import fr.greta.cda.s4_covoitmobile.exceptions.BookPlaceException;
import fr.greta.cda.s4_covoitmobile.exceptions.ResourceNotFoundException;
import fr.greta.cda.s4_covoitmobile.exceptions.RideAvailablePlaceInvalidException;
import fr.greta.cda.s4_covoitmobile.models.*;
import fr.greta.cda.s4_covoitmobile.repositories.RideRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class RideService
{
	private final RideRepository rideRepository;
	private final CityService cityService;
	
	private final UserService userService;
	private final CarService carService;
	
	private final ApplicationEventPublisher eventPublisher;
	
	
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
	
	public GetTripDetailResponse getTripDetail(final Long tripId)
	{
		Ride targetedRide = getTripInternal(tripId);
		
		User driver = targetedRide.getDriver();
		UserProfile driverProfile = driver.getUserProfile();
		
		final short availablePlaces = (short) (targetedRide.getAvailablePlace() -
											   targetedRide.getReservations().size());
		
		boolean isPrivileged = SecurityUtils.getAuthenticatedUser().isAdmin()
							   || checkIfAuthenticatedUserIsPartOfTheTrip(targetedRide);
		
		DetailedUserContactResponse.DetailedUserContactResponseBuilder driverBuilder = DetailedUserContactResponse
			.builder()
			.id(driver.getId())
			.firstName(driverProfile.getFirstname());
		
		if (isPrivileged)
		{
			driverBuilder
				.lastName(driverProfile.getLastname())
				.email(driver.getEmail())
				.phone(driverProfile.getPhone());
		}
		
		List<DetailedUserContactResponse> passengerContacts = new ArrayList<>();
		if (isPrivileged)
		{
			passengerContacts = extractPassengersFromRide(targetedRide).stream()
				.map(DetailedUserContactResponse::new)
				.toList();
		}
		
		return GetTripDetailResponse.builder()
			.kms(targetedRide.getDistanceKm())
			.departDate(targetedRide.getDepartDate())
			.availablePlaces(availablePlaces)
			.departureCityName(targetedRide.getDepartureCity().getName())
			.departureZip(targetedRide.getDepartureCity().getPostalCode())
			.arrivalCityName(targetedRide.getArrivalCity().getName())
			.arrivalZip(targetedRide.getArrivalCity().getPostalCode())
			.driverContact(driverBuilder.build())
			.passengerContacts(passengerContacts)
			.build();
	}
	
	@Transactional
	public void bookAPlaceOnRide(final Long tripId, final BookAPlaceOnRideRequest request)
	{
		SecurityUtils.checkOwnership(request.getPassengerId());
		
		Ride ride = getTripInternal(tripId);
		
		if (ride.getAvailablePlace() - ride.getReservations().size() <= 0)
		{
			throw new BookPlaceException("No more places available for this trip");
		}
		
		User potentialPassenger = userService.getUserDetail(request.getPassengerId());
		
		if (checkIfUserIsPartOfTheTrip(ride, potentialPassenger))
		{
			throw new BookPlaceException(
				String.format("%s %s %s", "User ", request.getPassengerId(), " is already present in the trip"));
		}
		
		PassengerReservation newReservation = new PassengerReservation();
		newReservation.setCancelled(false);
		newReservation.setPassenger(potentialPassenger);
		
		ride.addReservation(newReservation);
		
		rideRepository.save(ride);
	}
	
	@Transactional
	public void cancelRide(final Long tripId)
	{
		Ride ride = getTripInternal(tripId);
		
		SecurityUtils.checkOwnership(ride.getDriver().getId());
		
		ride.setCancelled(true);
		
		List<String> passengerEmails = new ArrayList<>(ride.getReservations().size());
		ride.getReservations().forEach(res ->
		{
			res.setCancelled(true);
			passengerEmails.add(res.getPassenger().getEmail());
		});
		
		rideRepository.save(ride);
		
		
		if (!passengerEmails.isEmpty())
		{
			eventPublisher.publishEvent(new RideCanceledEvent(passengerEmails, ride.getDepartDate().toString()));
		}
	}
	
	private Ride getTripInternal(Long tripId)
	{
		return rideRepository.findByIdAndIsCancelledFalse(tripId)
			.orElseThrow(() -> new ResourceNotFoundException("Trip", "Id", tripId));
	}
	
	private boolean checkIfAuthenticatedUserIsPartOfTheTrip(final Ride targetedRide)
	{
		User authenticatedUser = SecurityUtils.getAuthenticatedUser().getUser();
		
		return checkIfUserIsPartOfTheTrip(targetedRide, authenticatedUser);
	}
	
	private boolean checkIfUserIsPartOfTheTrip(final Ride targetedRide, final User authenticatedUser)
	{
		if (targetedRide.getDriver().getId().equals(authenticatedUser.getId()))
		{return true;}
		
		List<User> passengers = extractPassengersFromRide(targetedRide);
		
		for (User passenger : passengers)
		{
			if (passenger.getId().equals(authenticatedUser.getId()))
			{return true;}
		}
		return false;
	}
	
	private List<User> extractPassengersFromRide(final Ride targetedRide)
	{
		List<User> extractedPassengerList = new ArrayList<>();
		List<PassengerReservation> reservations = targetedRide.getReservations();
		
		for (PassengerReservation reservation : reservations)
		{
			extractedPassengerList.add(reservation.getPassenger());
		}
		return extractedPassengerList;
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
