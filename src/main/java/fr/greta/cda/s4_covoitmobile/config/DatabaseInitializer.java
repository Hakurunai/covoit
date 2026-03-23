package fr.greta.cda.s4_covoitmobile.config;

import fr.greta.cda.s4_covoitmobile.data.EAccountRole;
import fr.greta.cda.s4_covoitmobile.data.EAccountStatus;
import fr.greta.cda.s4_covoitmobile.dto.user.userprofile.CreateUserProfileRequest;
import fr.greta.cda.s4_covoitmobile.exceptions.AlreadyExistException;
import fr.greta.cda.s4_covoitmobile.models.CarBrand;
import fr.greta.cda.s4_covoitmobile.models.User;
import fr.greta.cda.s4_covoitmobile.services.AccountRoleService;
import fr.greta.cda.s4_covoitmobile.services.AccountStatusService;
import fr.greta.cda.s4_covoitmobile.services.CarService;
import fr.greta.cda.s4_covoitmobile.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class DatabaseInitializer implements CommandLineRunner
{
	private final AccountRoleService accountRoleService;
	private final AccountStatusService accountStatusService;
	
	private final UserService userService;
	private final CarService carService;
	
	private final Environment env;
	@Value("${covoit.app.defaultAdminEmail}")
	private String defaultAdminEmail;
	@Value("${covoit.app.defaultAdminPassword}")
	private String defaultAdminPassword;
	
	@Override
	public void run(final String... args)
	{
		log.info("Start database initialization");
		boolean isProduction = List.of(env.getActiveProfiles()).contains("prod");
		
		initRole();
		initStatus();
		initCarBrand(isProduction);
		initDefaultAdmin();
		initDefaultUsers(isProduction);
		
		log.info("End database initialization");
	}
	
	private void initCarBrand(boolean isAppInProd)
	{
		if (!isAppInProd)
		{
			carService.silentlyDeleteCarBrand("ANameUsedToTestTheAPI");
			carService.silentlyDeleteCarBrand("UpdatedNameToTestTheAPI");
		}
		
		if (carService.isEmpty())
		{
			log.info("Loading car brand from csv");
			
			
			try (InputStream inputStream = getClass().getResourceAsStream("/data/car_brands.csv");
				BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8)))
			{
				List<CarBrand> brands = reader.lines()
					.map(String::trim)
					.filter(line -> !line.isEmpty())
					.distinct()
					.map(CarBrand::new)
					.toList();
				
				carService.saveAll(brands);
				log.info("{} brand inserted with success", brands.size());
			}
			catch (Exception e)
			{
				log.error("Error while working on car_brands.csv : {}", e.getMessage());
			}
		}
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
		initProfile(defaultAdminEmail, "Admin Firstname", "Admin Lastname", "0000000000");
		initAdminCar(defaultAdminEmail);
	}
	
	private void initAdminCar(String profileMail)
	{
		User user = userService.getUserByMail(profileMail);
		
		if (!carService.getCarFromUserProfile(user.getId()).isEmpty())
		{return;}
		
		final Long brandId = 3L;
		final short nbSeats = 8;
		carService.saveCarInternal(brandId, user.getId(), "Admin model", "WWZZWWZZWW", nbSeats);
	}
	
	private void initDefaultUsers(boolean isAppInProd)
	{
		if (isAppInProd)
		{
			return;
		}
		final String DEFAULT_USER_MAIL = "user@test.fr";
		
		
		List<UserTestData> testUsers = List.of(
			new UserTestData(DEFAULT_USER_MAIL, EAccountStatus.ACTIVE),
			new UserTestData("userToDeleteByAdmin@test.fr", EAccountStatus.ACTIVE),
			new UserTestData("pendingUser@test.fr", EAccountStatus.PENDING),
			new UserTestData("deletedUser@test.fr", EAccountStatus.DELETED)
		);
		final String testUserPwd = "password123";
		
		log.info("Test users loading start (not included in prod)");
		for (UserTestData data : testUsers)
		{
			removeUser(data.mail());
			initUser(data.mail(), testUserPwd, List.of(EAccountRole.ROLE_USER), data.status());
		}
		
		removeUser("userNonValid@mail.fr");
		
		initDefaultProfile(DEFAULT_USER_MAIL);
		
		log.info("Those test users are not present in production");
	}
	
	
	private void initProfile(String profileMail, String firstname, String lastName, String phoneNumber)
	{
		User user = userService.getUserByMail(profileMail);
		
		if (userService.isProfileExisting(user.getId()))
		{return;}
		
		CreateUserProfileRequest data = new CreateUserProfileRequest();
		data.setFirstname(firstname);
		data.setLastname(lastName);
		data.setPhone(phoneNumber);
		
		userService.createNewProfile(user.getId(), data);
	}
	
	
	private void initDefaultProfile(String profileMail)
	{
		initProfile(profileMail, "John", "Doe", "0607080910");
		log.info("Default user profile inserted");
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