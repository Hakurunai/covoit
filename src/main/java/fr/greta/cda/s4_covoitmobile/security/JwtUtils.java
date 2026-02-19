package fr.greta.cda.s4_covoitmobile.security;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtils
{
	@Value("${covoit.app.jwtSecret}")
	private String jwtSecret;
	
	@Value("${covoit.app.jwtExpirationMs}")
	private int jwtExpirationMs;
	
	public String generateJwtToken(Authentication authentication) {
		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
		
		return Jwts.builder()
			.setSubject((userPrincipal.getUsername()))
			.setIssuedAt(new Date())
			.setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
			.signWith(key(), SignatureAlgorithm.HS256)
			.compact();
	}
	
	private Key key()
	{
		return Keys.hmacShaKeyFor(jwtSecret.getBytes());
	}
	
	// Validation du Token
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
}
