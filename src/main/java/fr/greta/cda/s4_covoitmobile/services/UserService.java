package fr.greta.cda.s4_covoitmobile.services;

import fr.greta.cda.s4_covoitmobile.data.EAccountRole;
import fr.greta.cda.s4_covoitmobile.data.EAccountStatus;
import fr.greta.cda.s4_covoitmobile.models.User;
import fr.greta.cda.s4_covoitmobile.repositories.AccountRoleRepository;
import fr.greta.cda.s4_covoitmobile.repositories.AccountStatusRepository;
import fr.greta.cda.s4_covoitmobile.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService
{
	private final UserRepository userRepository;
	private final AccountStatusRepository accountStatusRepository;
	private final AccountRoleRepository accountRoleRepository;
	
	private final PasswordEncoder passwordEncoder;
	
	@Transactional
	public User registerNewUser(String mail, String password)
	{
		User newUser = new User();
		newUser.setLogin(mail);
		newUser.setPassword(passwordEncoder.encode(password));
		
		accountRoleRepository.findByName(EAccountRole.ROLE_USER)
			.ifPresentOrElse(
				role -> newUser.getRoles().add(role),
				() ->
				{
					throw new RuntimeException("Error : Role not found");
				});
		
		accountStatusRepository.findByName(EAccountStatus.PENDING)
			.ifPresentOrElse(
				newUser::setAccountStatus,
				() ->
				{
					throw new RuntimeException("Error : Status not found");
				});
		
		return userRepository.save(newUser);
	}
}
