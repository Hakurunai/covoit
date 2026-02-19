package fr.greta.cda.s4_covoitmobile.config;

import fr.greta.cda.s4_covoitmobile.data.EAccountRole;
import fr.greta.cda.s4_covoitmobile.security.AuthTokenFilter;
import fr.greta.cda.s4_covoitmobile.security.UserDetailServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig
{
	private final UserDetailServiceImpl userDetailService;
	private final AuthTokenFilter authTokenFilter;
	
	@Bean
	public DaoAuthenticationProvider authenticationProvider()
	{
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailService);
		authProvider.setPasswordEncoder(passwordEncoder());
		return authProvider;
	}
	
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig)
	{
		return authConfig.getAuthenticationManager();
	}
	
	@Bean
	public PasswordEncoder passwordEncoder()
	{
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public RoleHierarchy roleHierarchy()
	{
		return RoleHierarchyImpl.withDefaultRolePrefix()
			.role(EAccountRole.ADMIN)
			.implies(EAccountRole.MODERATOR)
			.role(EAccountRole.MODERATOR)
			.implies(EAccountRole.USER)
			.build();
	}
	
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http)
	{
		http
			.csrf(csrf -> csrf.disable()) //we made an api, no need for us here, same for session
			.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.authorizeHttpRequests(
				auth -> auth
					.requestMatchers("/api/user/create").permitAll()
					.requestMatchers("/api/auth/**").permitAll()
					.anyRequest().authenticated()
			);
		
		http.authenticationProvider(authenticationProvider());
		http.addFilterBefore(authTokenFilter, UsernamePasswordAuthenticationFilter.class);
		
		return http.build();
	}
}
