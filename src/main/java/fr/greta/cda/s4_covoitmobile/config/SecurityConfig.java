package fr.greta.cda.s4_covoitmobile.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
			// 1. Désactive la protection CSRF pour permettre les POST via Postman
			.csrf(csrf -> csrf.disable())
			
			// 2. Autorise tout le monde à créer un compte sans être logué
			.authorizeHttpRequests(auth -> auth
											   .requestMatchers("/api/user/create").permitAll()
											   .anyRequest().authenticated()
								  )
			
			// 3. Active l'authentification Basic (celle que tu utilises dans Postman)
			.httpBasic(withDefaults());
		
		return http.build();
	}
}