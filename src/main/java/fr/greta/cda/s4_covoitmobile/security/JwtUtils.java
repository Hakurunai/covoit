package fr.greta.cda.s4_covoitmobile.security;

import fr.greta.cda.s4_covoitmobile.models.User;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Duration;
import java.util.Date;

@Component
public class JwtUtils
{
	@Value("${covoit.app.jwtSecret}")
	private String jwtSecret;
	
	@Value("${covoit.app.jwtExpirationMs}")
	private Duration jwtExpiration;
	
	public String generateJwtToken(Authentication authentication)
	{
		if (authentication == null || !authentication.isAuthenticated())
		{
			throw new IllegalArgumentException("User need to be authenticated to generate a JWT");
		}
		
		Object principal = authentication.getPrincipal();
		
		if (!(principal instanceof UserDetailsImpl userPrincipal))
		{
			throw new IllegalStateException("Principal is not an instance of UserDetailsImpl");
		}
		
		return buildToken(userPrincipal.getId(), userPrincipal.getUsername());
	}
	
	public String generateTokenFromUser(User user)
	{
		return buildToken(user.getId(), user.getEmail());
	}
	
	private String buildToken(Long userId, String username)
	{
		return Jwts.builder()
			.subject(username)
			.claim("id", userId)
			.issuedAt(new Date())
			.expiration(new Date((new Date()).getTime() + jwtExpiration.toMillis()))
			.signWith(key())
			.compact();
	}
	
	public boolean validateJwtToken(String authToken)
	{
		try
		{
			Jwts.parser().verifyWith((javax.crypto.SecretKey) key()).build().parseSignedClaims(authToken);
			return true;
		}
		catch (JwtException | IllegalArgumentException e)
		{
			System.err.println("JWT Error: " + e.getMessage());
		}
		return false;
	}
	
	public String getUserNameFromJwtToken(String token)
	{
		return Jwts.parser()
			.verifyWith((javax.crypto.SecretKey) key())
			.build()
			.parseSignedClaims(token)
			.getPayload()
			.getSubject();
	}
	
	private Key key()
	{
		return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
	}
}
