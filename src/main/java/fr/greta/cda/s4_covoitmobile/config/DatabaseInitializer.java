package fr.greta.cda.s4_covoitmobile.config;

import fr.greta.cda.s4_covoitmobile.data.EAccountRole;
import fr.greta.cda.s4_covoitmobile.data.EAccountStatus;
import fr.greta.cda.s4_covoitmobile.exceptions.AlreadyExistException;
import fr.greta.cda.s4_covoitmobile.services.AccountRoleService;
import fr.greta.cda.s4_covoitmobile.services.AccountStatusService;
import fr.greta.cda.s4_covoitmobile.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class DatabaseInitializer implements CommandLineRunner
{
	private final AccountRoleService accountRoleService;
	private final AccountStatusService accountStatusService;
	
	private final UserService userService;
	
	
	private final Environment env;
	@Value("${covoit.app.defaultAdminEmail}")
	private String defaultAdminEmail;
	@Value("${covoit.app.defaultAdminPassword}")
	private String defaultAdminPassword;
	
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
		accountRoleService.initalizeRoles();
	}
	
	private void initStatus()
	{
		accountStatusService.initalizeStatus();
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
		
		log.info("Test users loading start (not included in prod)");
		for (UserTestData data : testUsers)
		{
			removeUser(data.mail());
			initUser(data.mail(), testUserPwd, List.of(EAccountRole.ROLE_USER), data.status());
		}
		
		removeUser("userNonValid@mail.fr");
		log.info("Those test users are not present in production");
	}
	
	private void removeUser(final String mail)
	{
		if (!userService.existByMail(mail))
		{return;}
		userService.deleteUserByMail(mail);
	}
	
	
	private void initUser(String mail, String pwd, List<EAccountRole> roles, EAccountStatus status)
	{
		if (userService.existByMail(mail))
		{
			log.info("User connexion identifiers : mail = {} | pwd = {}", mail, pwd);
			return;
		}
		
		try
		{
			userService.testRegisterUser(mail, pwd, roles, status);
			log.info("User created with identifiers : mail = {} | pwd = {}", mail, pwd);
		}
		catch (AlreadyExistException ex)
		{
			log.warn("Init default database user fail : {}", ex.getMessage());
		}
	}
}

record UserTestData(String mail, EAccountStatus status) {}