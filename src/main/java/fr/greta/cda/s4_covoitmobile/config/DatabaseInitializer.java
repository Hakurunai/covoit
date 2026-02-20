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
	
	@Value("${covoit.app.defaultAdminEmail}")
	private String adminEmail;
	
	@Value("${covoit.app.defaultAdminPassword}")
	private String adminPassword;
	
	@Transactional
	@Override
	public void run(final String... args)
	{
		log.info("Start database initialization");
		
		initRole();
		initStatus();
		initDefaultAdmin();
		
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
		if (userRepository.findByEmail(adminEmail).isEmpty())
		{
			userService.registerNewUser(
				adminEmail,
				adminPassword,
				List.of(EAccountRole.ROLE_ADMIN),
				EAccountStatus.ACTIVE
			);
			log.info("Default administrator account created : {}", adminEmail);
		}
		else
		{
			log.info("Default administrator mail : {}", adminEmail);
		}
	}
}
