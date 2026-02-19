package fr.greta.cda.s4_covoitmobile.services;

import fr.greta.cda.s4_covoitmobile.models.RefreshToken;
import fr.greta.cda.s4_covoitmobile.models.User;
import fr.greta.cda.s4_covoitmobile.repositories.RefreshTokenRepository;
import fr.greta.cda.s4_covoitmobile.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService
{
	@Value("${covoit.app.jwtRefreshExpiration}")
	private Duration refreshTokenDuration;
	
	private final RefreshTokenRepository refreshTokenRepository;
	private final UserRepository userRepository;
	
	public Optional<RefreshToken> findByToken(String token)
	{
		return refreshTokenRepository.findByToken(token);
	}
	
	@Transactional
	public RefreshToken createRefreshToken(Long userId)
	{
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new RuntimeException("User id invalid"));
		
		//remove older token if exist
		refreshTokenRepository.deleteByUser(user);
		refreshTokenRepository.flush();
		
		
		RefreshToken refreshToken = new RefreshToken();
		refreshToken.setUser(user);
		refreshToken.setExpiryDate(Instant.now().plus(refreshTokenDuration));
		refreshToken.setToken(UUID.randomUUID().toString());
		
		return refreshTokenRepository.save(refreshToken);
	}
	
	public RefreshToken verifyExpiration(RefreshToken token)
	{
		if (token.getExpiryDate().compareTo(Instant.now()) < 0)
		{
			refreshTokenRepository.delete(token);
			throw new RuntimeException("Refresh token was expired. Please make a new signin request");
		}
		return token;
	}
}
