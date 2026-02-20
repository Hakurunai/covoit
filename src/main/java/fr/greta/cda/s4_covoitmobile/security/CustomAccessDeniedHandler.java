package fr.greta.cda.s4_covoitmobile.security;

import fr.greta.cda.s4_covoitmobile.dto.ErrorMessageResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class CustomAccessDeniedHandler implements AccessDeniedHandler
{
	private final RoleHierarchy roleHierarchy;
	private final ObjectMapper objectMapper; //jackson mapper
	
	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response,
		AccessDeniedException accessDeniedException) throws IOException
	{
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		String message = "Access refused : insufficient privileges";
		String accountStatus = "UNKNOWN";
		
		if (auth != null && auth.getPrincipal() instanceof UserDetailsImpl userDetails)
		{
			accountStatus = userDetails.getUser().getAccountStatus().getName().name();
			
			Collection<? extends GrantedAuthority> reachableAuthorities =
				roleHierarchy.getReachableGrantedAuthorities(auth.getAuthorities());
			
			boolean hasUserRole = reachableAuthorities.stream()
				.anyMatch(a -> Objects.equals(a.getAuthority(), "ROLE_USER"));
			boolean isAccountValid = reachableAuthorities.stream()
				.anyMatch(a -> Objects.equals(a.getAuthority(), "ROLE_USER_VALIDATED"));
			
			if (hasUserRole && !isAccountValid)
			{
				message = "Your account is not validated";
			}
		}
		
		ErrorMessageResponse errorResponse = new ErrorMessageResponse(
			HttpServletResponse.SC_FORBIDDEN,
			LocalDateTime.now(),
			message,
			accountStatus
		);
		
		response.setContentType("application/json;charset=UTF-8");
		response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		
		response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
	}
}
