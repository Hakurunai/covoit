package fr.greta.cda.s4_covoitmobile.exceptions;

public class TokenRefreshException extends RuntimeException
{
	public TokenRefreshException(String token, String message)
	{
		super(String.format("Token refresh failed for [%s] : [%s]", token, message));
	}
}
