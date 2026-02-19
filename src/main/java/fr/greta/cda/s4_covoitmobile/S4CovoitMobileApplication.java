package fr.greta.cda.s4_covoitmobile;

import fr.greta.cda.s4_covoitmobile.data.EAccountRole;
import fr.greta.cda.s4_covoitmobile.data.EAccountStatus;
import fr.greta.cda.s4_covoitmobile.models.AccountRole;
import fr.greta.cda.s4_covoitmobile.models.AccountStatus;
import fr.greta.cda.s4_covoitmobile.repositories.AccountRoleRepository;
import fr.greta.cda.s4_covoitmobile.repositories.AccountStatusRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class S4CovoitMobileApplication
{
	public static void main(String[] args)
	{
		SpringApplication.run(S4CovoitMobileApplication.class, args);
	}
	
	
	@Bean
	CommandLineRunner initDatabase(AccountRoleRepository roleRepo, AccountStatusRepository statusRepo) {
		return args -> {
			for (EAccountRole roleEnum : EAccountRole.values())
			{
				if (roleRepo.findByName(roleEnum).isEmpty())
				{
					AccountRole r = new AccountRole();
					r.setName(roleEnum);
					roleRepo.save(r);
					System.out.println("Add missing role in database : " + roleEnum);
				}
			}
			
			for (EAccountStatus statusEnum : EAccountStatus.values())
			{
				if (statusRepo.findByName(statusEnum).isEmpty())
				{
					AccountStatus s = new AccountStatus();
					s.setName(statusEnum);
					statusRepo.save(s);
					System.out.println("Add missing status in database : " + statusEnum);
				}
			}
		};
	}
}
