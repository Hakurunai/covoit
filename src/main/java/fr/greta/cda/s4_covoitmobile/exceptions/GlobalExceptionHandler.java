package fr.greta.cda.s4_covoitmobile.exceptions;

import fr.greta.cda.s4_covoitmobile.dtos.ErrorMessageDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler
{
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ErrorMessageDto handleResourceNotFound(ResourceNotFoundException ex)
	{
		log.warn("Resource not found : {}}", ex.getMessage());
		
		return new ErrorMessageDto(
			HttpStatus.NOT_FOUND.value(),
			LocalDateTime.now(),
			"Resource not found"
		);
	}
	
	@ExceptionHandler(AuthorizationDeniedException.class)
	@ResponseStatus(HttpStatus.FORBIDDEN)
	public ErrorMessageDto handleBadCredentials(AuthorizationDeniedException ex)
	{
		log.info("AuthorizationDeniedException : {}", ex.getMessage());
		
		return new ErrorMessageDto(
			HttpStatus.FORBIDDEN.value(),
			LocalDateTime.now(),
			"Your rights does not allow you the access of this resource"
		);
	}
	
	@ExceptionHandler(BadCredentialsException.class)
	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	public ErrorMessageDto handleBadCredentials(BadCredentialsException ex)
	{
		log.info("BadCredentialsException : {}", ex.getMessage());
		
		return new ErrorMessageDto(
			HttpStatus.UNAUTHORIZED.value(),
			LocalDateTime.now(),
			"Id or password incorrect"
		);
	}
	
	@ExceptionHandler(TokenRefreshException.class)
	@ResponseStatus(HttpStatus.FORBIDDEN)
	public ErrorMessageDto handleTokenRefreshException(TokenRefreshException ex, WebRequest request)
	{
		log.warn("TokenRefreshException : {}", ex.getMessage());
		
		return new ErrorMessageDto(
			HttpStatus.FORBIDDEN.value(),
			LocalDateTime.now(),
			ex.getMessage());
	}
	
	/**
	 * Our safety net
	 *
	 * @param ex      the exception thrown
	 * @param request the request causing the exception
	 * @return a JSON dtos with a generic message
	 */
	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ErrorMessageDto handleGlobalException(Exception ex, WebRequest request)
	{
		log.error("Unhandled exception : ", ex);
		
		return new ErrorMessageDto(
			HttpStatus.INTERNAL_SERVER_ERROR.value(),
			LocalDateTime.now(),
			"An internal error occurred. If the issue subsist, please contact our support.");
	}
}