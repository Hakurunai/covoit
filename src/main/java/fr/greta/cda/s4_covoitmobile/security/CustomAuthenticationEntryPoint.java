package fr.greta.cda.s4_covoitmobile.security;

import fr.greta.cda.s4_covoitmobile.dto.ErrorMessageResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint
{
	private final ObjectMapper objectMapper;
	
	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
		AuthenticationException authException) throws IOException
	{
		String message = (String) request.getAttribute("jwt_error");
		
		if (message == null)
		{
			message = "Full authentication is required to access this resource";
		}
		
		ErrorMessageResponse errorResponse = new ErrorMessageResponse(
			HttpServletResponse.SC_UNAUTHORIZED,
			LocalDateTime.now(),
			message,
			"NOT_AUTHENTICATED"
		);
		
		response.setContentType("application/json;charset=UTF-8");
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
	}
}
