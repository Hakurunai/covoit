package fr.greta.cda.s4_covoitmobile.services;

import fr.greta.cda.s4_covoitmobile.data.EAccountRole;
import fr.greta.cda.s4_covoitmobile.data.EAccountStatus;
import fr.greta.cda.s4_covoitmobile.models.AccountRole;
import fr.greta.cda.s4_covoitmobile.models.User;
import fr.greta.cda.s4_covoitmobile.repositories.AccountRoleRepository;
import fr.greta.cda.s4_covoitmobile.repositories.AccountStatusRepository;
import fr.greta.cda.s4_covoitmobile.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService
{
	private final UserRepository userRepository;
	private final AccountStatusRepository accountStatusRepository;
	private final AccountRoleRepository accountRoleRepository;
	
	@Autowired
	public UserService(final UserRepository userRepository, final AccountStatusRepository accountStatusRepository,
					   final AccountRoleRepository accountRoleRepository)
	{
		this.userRepository = userRepository;
		this.accountStatusRepository = accountStatusRepository;
		this.accountRoleRepository = accountRoleRepository;
	}
	
	@Transactional
	public User registerNewUser(String mail, String password)
	{
		User newUser = new User();
		newUser.setLogin(mail);
		newUser.setPassword(password);
		
		accountRoleRepository.findByName(EAccountRole.ROLE_USER)
			.ifPresent(role -> newUser.getRoles().add(role));
		
		accountStatusRepository.findByName(EAccountStatus.PENDING)
			.ifPresent(newUser::setAccountStatus);
		
		return userRepository.save(newUser);
	}
}
