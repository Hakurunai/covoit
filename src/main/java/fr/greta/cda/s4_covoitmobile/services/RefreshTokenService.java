package fr.greta.cda.s4_covoitmobile.services;

import fr.greta.cda.s4_covoitmobile.exceptions.ResourceNotFoundException;
import fr.greta.cda.s4_covoitmobile.exceptions.TokenRefreshException;
import fr.greta.cda.s4_covoitmobile.models.RefreshToken;
import fr.greta.cda.s4_covoitmobile.models.User;
import fr.greta.cda.s4_covoitmobile.repositories.RefreshTokenRepository;
import fr.greta.cda.s4_covoitmobile.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenService
{
	@Value("${covoit.app.jwtRefreshExpiration}")
	private Duration refreshTokenDuration;
	
	private final RefreshTokenRepository refreshTokenRepository;
	private final UserRepository userRepository;
	
	@Transactional
	public RefreshToken createRefreshToken(Long userId)
	{
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
		
		//remove older token if exist
		refreshTokenRepository.deleteByUser(user);
		refreshTokenRepository.flush();
		
		
		RefreshToken refreshToken = new RefreshToken();
		refreshToken.setUser(user);
		refreshToken.setExpiryDate(Instant.now().plus(refreshTokenDuration));
		refreshToken.setToken(UUID.randomUUID().toString());
		
		RefreshToken newToken = refreshTokenRepository.save(refreshToken);
		log.info("Refresh token created for user ID : {}", userId);
		return newToken;
	}
	
	public RefreshToken findByToken(String token)
	{
		return refreshTokenRepository
			.findByToken(token)
			.orElseThrow(() -> new TokenRefreshException(token, "Unknown or revoked token refresh used"));
	}
	
	public void verifyExpiration(RefreshToken token)
	{
		if (token.getExpiryDate().compareTo(Instant.now()) < 0)
		{
			refreshTokenRepository.delete(token);
			throw new TokenRefreshException(token.getToken(), "Refresh token as expired");
		}
	}
	
	@Transactional
	public void deleteByUserId(Long userId)
	{
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
		
		refreshTokenRepository.deleteByUser(user);
		log.info("Refresh Token deleted for user ID : {}", userId);
	}
}
