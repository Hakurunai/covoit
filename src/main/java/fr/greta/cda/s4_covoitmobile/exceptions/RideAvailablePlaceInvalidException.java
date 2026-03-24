package fr.greta.cda.s4_covoitmobile.exceptions;

public class RideAvailablePlaceInvalidException extends RuntimeException
{
	public RideAvailablePlaceInvalidException(Long driverId, final short availablePlace, final short carPlace)
	{
		super("User " + driverId + " cannot create a trip offering " +
						 availablePlace + " with a vehicle with only " + carPlace + " car places");
	}
}
