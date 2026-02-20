package fr.greta.cda.s4_covoitmobile.config;

import fr.greta.cda.s4_covoitmobile.data.EAccountRole;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;

@Configuration
public class RoleConfig
{
	@Bean
	public RoleHierarchy roleHierarchy()
	{
		return RoleHierarchyImpl.withDefaultRolePrefix()
			.role(EAccountRole.ADMIN)
			.implies(EAccountRole.USER)
			.build();
	}
}
