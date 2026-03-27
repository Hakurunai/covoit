package fr.greta.cda.s4_covoitmobile.services;

import fr.greta.cda.s4_covoitmobile.dto.trip.CreateTripRequest;
import fr.greta.cda.s4_covoitmobile.dto.trip.GetTripDetailResponse;
import fr.greta.cda.s4_covoitmobile.dto.trip.book.BookAPlaceOnRideRequest;
import fr.greta.cda.s4_covoitmobile.dto.trip.book.CancelBookRequest;
import fr.greta.cda.s4_covoitmobile.dto.user.DetailedUserContactResponse;
import fr.greta.cda.s4_covoitmobile.event.RideCanceledEvent;
import fr.greta.cda.s4_covoitmobile.exceptions.BookCancellationException;
import fr.greta.cda.s4_covoitmobile.exceptions.BookPlaceException;
import fr.greta.cda.s4_covoitmobile.exceptions.ResourceNotFoundException;
import fr.greta.cda.s4_covoitmobile.exceptions.RideAvailablePlaceInvalidException;
import fr.greta.cda.s4_covoitmobile.models.*;
import fr.greta.cda.s4_covoitmobile.repositories.RideRepository;
import fr.greta.cda.s4_covoitmobile.security.UserDetailsImpl;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authorization.AuthorizationDeniedException;
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
		
		if (ride.getAvailablePlace() - ride.getActiveReservations().size() <= 0)
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
		
		List<String> passengerEmails = new ArrayList<>(ride.getActiveReservations().size());
		ride.getReservations().forEach(res ->
		{
			if (!res.isCancelled())
			{
				res.setCancelled(true);
				passengerEmails.add(res.getPassenger().getEmail());
			}
		});
		
		rideRepository.save(ride);
		
		
		if (!passengerEmails.isEmpty())
		{
			eventPublisher.publishEvent(new RideCanceledEvent(passengerEmails, ride.getDepartDate().toString()));
		}
	}
	
	@Transactional
	public void cancelBookOnTrip(final Long tripId, final CancelBookRequest request)
	{
		Ride ride = getTripInternal(tripId);
		
		if (!isAPassengerInTheRide(ride, request.getPassengerId()))
		{
			throw new BookCancellationException(
				"Passenger " + request.getPassengerId() + " was not found on trip " + tripId);
		}
		
		if (ride.getDriver().getId().equals(request.getPassengerId()))
		{
			throw new BookCancellationException("A driver on a Ride cannot be removed from her as a passenger");
		}
		
		final UserDetailsImpl authenticatedUser = SecurityUtils.getAuthenticatedUser();
		final Long authUserId = authenticatedUser.getId();
		
		if (authenticatedUser.isAdmin() ||
			authUserId.equals(ride.getDriver().getId()) ||
			isAPassengerInTheRide(ride, authUserId))
		{
			PassengerReservation reservation = ride.getReservations().stream()
				.filter(res -> res.getPassenger().getId().equals(request.getPassengerId()))
				.filter(res -> !res.isCancelled())
				.findFirst()
				.orElseThrow(() -> new BookCancellationException("Active reservation not found"));
			
			reservation.setCancelled(true);
			
			rideRepository.save(ride);
		}
		else
		{
			throw new AuthorizationDeniedException("User " + authUserId + " cannot remove user " +
												   request.getPassengerId() + " from Ride " + ride.getId());
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
		List<PassengerReservation> reservations = targetedRide.getActiveReservations();
		
		for (PassengerReservation reservation : reservations)
		{
			extractedPassengerList.add(reservation.getPassenger());
		}
		return extractedPassengerList;
	}
	
	private boolean isAPassengerInTheRide(final Ride targetedRide, final Long passengerId)
	{
		List<User> passengers = extractPassengersFromRide(targetedRide);
		for (User passenger : passengers)
		{
			if (passenger.getId().equals(passengerId))
			{return true;}
		}
		return false;
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
}
