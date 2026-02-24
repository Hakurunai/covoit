package fr.greta.cda.s4_covoitmobile.config;

import fr.greta.cda.s4_covoitmobile.data.EAccountRole;
import fr.greta.cda.s4_covoitmobile.data.EAccountStatus;
import fr.greta.cda.s4_covoitmobile.models.AccountRole;
import fr.greta.cda.s4_covoitmobile.models.AccountStatus;
import fr.greta.cda.s4_covoitmobile.repositories.AccountRoleRepository;
import fr.greta.cda.s4_covoitmobile.repositories.AccountStatusRepository;
import fr.greta.cda.s4_covoitmobile.repositories.UserRepository;
import fr.greta.cda.s4_covoitmobile.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class DatabaseInitializer implements CommandLineRunner
{
	private final AccountRoleRepository roleRepo;
	private final AccountStatusRepository statusRepo;
	
	private final UserRepository userRepository;
	private final UserService userService;
	
	private final Environment env;
	@Value("${covoit.app.defaultAdminEmail}")
	private String defaultAdminEmail;
	@Value("${covoit.app.defaultAdminPassword}")
	private String defaultAdminPassword;
	
	@Transactional
	@Override
	public void run(final String... args)
	{
		log.info("Start database initialization");
		
		initRole();
		initStatus();
		initDefaultAdmin();
		
		boolean isProduction = List.of(env.getActiveProfiles()).contains("prod");
		if (!isProduction)
		{
			initDefaultUser();
		}
		
		log.info("End database initialization");
	}
	
	private void initRole()
	{
		for (EAccountRole roleEnum : EAccountRole.values())
		{
			if (roleRepo.findByName(roleEnum).isEmpty())
			{
				AccountRole role = new AccountRole();
				role.setName(roleEnum);
				roleRepo.save(role);
				log.info("Add new role in database : {}", roleEnum);
			}
		}
	}
	
	private void initStatus()
	{
		for (EAccountStatus statusEnum : EAccountStatus.values())
		{
			if (statusRepo.findByName(statusEnum).isEmpty())
			{
				AccountStatus status = new AccountStatus();
				status.setName(statusEnum);
				statusRepo.save(status);
				log.info("Add new status in database : {}", statusEnum);
			}
		}
	}
	
	private void initDefaultAdmin()
	{
		if (userRepository.findByEmail(defaultAdminEmail).isEmpty())
		{
			userService.registerNewUser(
				defaultAdminEmail,
				defaultAdminPassword,
				List.of(EAccountRole.ROLE_ADMIN),
				EAccountStatus.ACTIVE
			);
			log.info("Default administrator account created : {}", defaultAdminEmail);
		}
		else
		{
			log.info("Default administrator mail : {}", defaultAdminEmail);
		}
	}
	
	private void initDefaultUser()
	{
		final String testEmail = "user@test.fr";
		final String testPwd = "password123";
		if (userRepository.findByEmail(testEmail).isEmpty())
		{
			userService.registerNewUser(
				testEmail,
				testPwd,
				List.of(EAccountRole.ROLE_USER),
				EAccountStatus.ACTIVE
			);
			log.info("Test user created with identifiers : mail = {} | pwd = {}", testEmail, testPwd);
		}
		else
		{
			log.info("Test user connexion identifiers : mail = {} | pwd = {}", testEmail, testPwd);
		}
		log.info("This user is not present in production");
	}
}
