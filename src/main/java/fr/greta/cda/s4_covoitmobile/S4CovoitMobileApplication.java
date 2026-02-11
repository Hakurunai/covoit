package fr.greta.cda.s4_covoitmobile;

import fr.greta.cda.s4_covoitmobile.data.EAccountRole;
import fr.greta.cda.s4_covoitmobile.models.AccountRole;
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
			if (roleRepo.count() == 0) {
				for (EAccountRole role : EAccountRole.values()) {
					AccountRole r = new AccountRole();
					r.setName(role);
					roleRepo.save(r);
				}
			}
		};
	}
}
