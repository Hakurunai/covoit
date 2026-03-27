package fr.greta.cda.s4_covoitmobile.services;

import fr.greta.cda.s4_covoitmobile.security.UserDetailsImpl;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class SecurityUtils
{
	private SecurityUtils() {}

	public static UserDetailsImpl getAuthenticatedUser()
	{
		return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
			.map(Authentication::getPrincipal)
			.filter(UserDetailsImpl.class::isInstance)
			.map(UserDetailsImpl.class::cast)
			.orElseThrow(() -> new AuthorizationDeniedException("Access denied"));
	}
	
	
	public static void checkOwnership(Long resourceOwnerId)
	{
		UserDetailsImpl currentUser = getAuthenticatedUser();
		
		if (!currentUser.canAccess(resourceOwnerId))
		{
			throw new AuthorizationDeniedException("You have no right to access this resource");
		}
	}
}