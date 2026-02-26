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
			initDefaultUsers();
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
				log.warn("Add new role in database : {}", roleEnum);
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
				log.warn("Add new status in database : {}", statusEnum);
			}
		}
	}
	
	private void initDefaultAdmin()
	{
		log.warn("Default administrator is loaded, look on next line to know his identifiers");
		initUser(defaultAdminEmail, defaultAdminPassword, List.of(EAccountRole.ROLE_ADMIN), EAccountStatus.ACTIVE);
	}
	
	private void initDefaultUsers()
	{
		List<UserTestData> testUsers = List.of(
			new UserTestData("user@test.fr", EAccountStatus.ACTIVE),
			new UserTestData("pendingUser@test.fr", EAccountStatus.PENDING),
			new UserTestData("suspendedUser@test.fr", EAccountStatus.SUSPENDED)
		);
		final String testUserPwd = "password123";
		
		log.info("Test users loading start.");
		for (UserTestData user : testUsers)
		{
			initUser(user.mail(), testUserPwd, List.of(EAccountRole.ROLE_USER), user.status());
		}
		log.info("Those test users are not present in production");
	}
	
	private void initUser(String mail, String pwd, List<EAccountRole> roles, EAccountStatus status)
	{
		if (userRepository.findByEmail(mail).isEmpty())
		{
			userService.registerNewUser(mail, pwd, roles, status);
			log.info("User created with identifiers : mail = {} | pwd = {}", mail, pwd);
		}
		else
		{
			log.info("User connexion identifiers : mail = {} | pwd = {}", mail, pwd);
		}
	}
}

record UserTestData(String mail, EAccountStatus status) {}