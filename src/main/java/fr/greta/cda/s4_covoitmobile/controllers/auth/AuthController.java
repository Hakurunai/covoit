package fr.greta.cda.s4_covoitmobile.controllers.auth;

import fr.greta.cda.s4_covoitmobile.dtos.LoginRequest;
import fr.greta.cda.s4_covoitmobile.dtos.LoginResponse;
import fr.greta.cda.s4_covoitmobile.dtos.TokenRefreshRequest;
import fr.greta.cda.s4_covoitmobile.dtos.TokenRefreshResponse;
import fr.greta.cda.s4_covoitmobile.models.RefreshToken;
import fr.greta.cda.s4_covoitmobile.security.JwtUtils;
import fr.greta.cda.s4_covoitmobile.security.UserDetailsImpl;
import fr.greta.cda.s4_covoitmobile.services.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController
{
	private final AuthenticationManager authenticationManager;
	private final JwtUtils jwtUtils;
	private final RefreshTokenService refreshTokenService;
	
	@PostMapping("/login")
	public ResponseEntity<LoginResponse> authenticateUser(@RequestBody LoginRequest loginRequest)
	{
		Authentication authentication = authenticationManager.authenticate(
			new UsernamePasswordAuthenticationToken(
				loginRequest.getLogin(),
				loginRequest.getPassword()
			)
		);
		
		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwt = jwtUtils.generateJwtToken(authentication);
		
		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
		assert (userDetails != null);
		
		RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());
		
		List<String> roles = userDetails.getAuthorities().stream()
			.map(GrantedAuthority::getAuthority)
			.toList();
		
		return ResponseEntity.ok(new LoginResponse(jwt, refreshToken.getToken(), userDetails.getUsername(), roles));
	}
	
	
	@PostMapping("/refreshtoken")
	public ResponseEntity<TokenRefreshResponse> refreshToken(@RequestBody TokenRefreshRequest request)
	{
		String requestRefreshToken = request.getRefreshToken();
		
		return refreshTokenService.findByToken(requestRefreshToken)
			.map(refreshTokenService::verifyExpiration)
			.map(RefreshToken::getUser)
			.map(user ->
			{
				String token = jwtUtils.generateTokenFromUsername(user.getLogin());
				return ResponseEntity.ok(new TokenRefreshResponse(token));
			})
			.orElseThrow(() -> new RuntimeException("Refresh token is not in database!"));
	}
}
