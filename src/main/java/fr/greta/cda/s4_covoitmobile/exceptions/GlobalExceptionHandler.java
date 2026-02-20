package fr.greta.cda.s4_covoitmobile.exceptions;

import fr.greta.cda.s4_covoitmobile.dto.ErrorMessageResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler
{
	@ExceptionHandler(ResourceNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ErrorMessageResponse handleResourceNotFound(ResourceNotFoundException ex)
	{
		log.warn("Resource not found : {}}", ex.getMessage());
		return buildError(HttpStatus.NOT_FOUND, "Resource not found");
	}
	
	@ExceptionHandler(AuthorizationDeniedException.class)
	@ResponseStatus(HttpStatus.FORBIDDEN)
	public ErrorMessageResponse handleAuthorizationDenied(AuthorizationDeniedException ex)
	{
		log.info("AuthorizationDeniedException : {}", ex.getMessage());
		return buildError(HttpStatus.FORBIDDEN, "Your rights does not allow you the access of this resource");
	}
	
	@ExceptionHandler(BadCredentialsException.class)
	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	public ErrorMessageResponse handleBadCredentials(BadCredentialsException ex)
	{
		log.info("BadCredentialsException : {}", ex.getMessage());
		return buildError(HttpStatus.UNAUTHORIZED, "Id or password incorrect");
	}
	
	@ExceptionHandler(TokenRefreshException.class)
	@ResponseStatus(HttpStatus.FORBIDDEN)
	public ErrorMessageResponse handleTokenRefreshException(TokenRefreshException ex)
	{
		log.warn("TokenRefreshException : {}", ex.getMessage());
		return buildError(HttpStatus.FORBIDDEN, ex.getMessage());
	}
	
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorMessageResponse handleValidationException(MethodArgumentNotValidException ex)
	{
		List<String> errors = ex.getBindingResult()
			.getFieldErrors()
			.stream()
			.map(error -> error.getField() + ": " + error.getDefaultMessage())
			.toList();
		
		String detailedMessage = String.join(", ", errors);
		
		log.warn("Validation failed: {}", detailedMessage);
		
		return buildError(HttpStatus.BAD_REQUEST, "Validation failed: " + detailedMessage);
	}
	
	@ExceptionHandler(AlreadyExistException.class)
	@ResponseStatus(HttpStatus.CONFLICT)
	public ErrorMessageResponse handleAlreadyExist(AlreadyExistException ex)
	{
		log.warn("AlreadyExistException : {}", ex.getMessage());
		return buildError(HttpStatus.CONFLICT, ex.getMessage());
	}
	
	/**
	 * Our safety net
	 *
	 * @param ex      the exception thrown
	 * @return a JSON dtos with a generic message
	 */
	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ErrorMessageResponse handleGlobalException(Exception ex)
	{
		log.error("Unhandled exception : ", ex);
		
		return buildError(HttpStatus.INTERNAL_SERVER_ERROR,
			"An internal error occurred. If the issue subsist, please contact our support.");
	}
	
	private ErrorMessageResponse buildError(HttpStatus status, String message)
	{
		return new ErrorMessageResponse(status.value(), LocalDateTime.now(), message, null);
	}
}