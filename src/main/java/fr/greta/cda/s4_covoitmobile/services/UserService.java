package fr.greta.cda.s4_covoitmobile.services;

import fr.greta.cda.s4_covoitmobile.data.EAccountRole;
import fr.greta.cda.s4_covoitmobile.data.EAccountStatus;
import fr.greta.cda.s4_covoitmobile.dto.user.GetAllUserResponse;
import fr.greta.cda.s4_covoitmobile.dto.userprofile.CreateUserProfileRequest;
import fr.greta.cda.s4_covoitmobile.exceptions.AlreadyExistException;
import fr.greta.cda.s4_covoitmobile.exceptions.ResourceNotFoundException;
import fr.greta.cda.s4_covoitmobile.models.AccountStatus;
import fr.greta.cda.s4_covoitmobile.models.User;
import fr.greta.cda.s4_covoitmobile.models.UserProfile;
import fr.greta.cda.s4_covoitmobile.repositories.AccountRoleRepository;
import fr.greta.cda.s4_covoitmobile.repositories.AccountStatusRepository;
import fr.greta.cda.s4_covoitmobile.repositories.UserProfileRepository;
import fr.greta.cda.s4_covoitmobile.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class UserService
{
	private final UserRepository userRepository;
	private final AccountStatusRepository accountStatusRepository;
	private final AccountRoleRepository accountRoleRepository;
	private final UserProfileRepository userProfileRepository;
	
	private final PasswordEncoder passwordEncoder;
	
	@Transactional
	public User registerNewUser(String mail, String password, List<EAccountRole> roles, EAccountStatus status)
	{
		if (userRepository.existsByEmail(mail))
		{
			throw new AlreadyExistException("Email '" + mail + "' is already used");
		}
		
		User newUser = new User();
		newUser.setEmail(mail);
		newUser.setPassword(passwordEncoder.encode(password));
		
		for (EAccountRole role : roles)
		{
			accountRoleRepository.findByName(role)
				.ifPresentOrElse(
					roleFound -> newUser.getRoles().add(roleFound),
					() -> {throw new ResourceNotFoundException("Role", "role", role.toString());}
				);
		}
		
		accountStatusRepository.findByName(status)
			.ifPresentOrElse(
				newUser::setAccountStatus,
				() -> {throw new ResourceNotFoundException("Status", "status", status.toString());}
			);
		
		return userRepository.save(newUser);
	}
	
	
	@Transactional
	public UserProfile completeProfile(Long userId, CreateUserProfileRequest dto)
	{
		if (userId == null)
		{
			throw new IllegalArgumentException("User ID must not be null");
		}
		
		if (userProfileRepository.existsById(userId))
		{
			throw new AlreadyExistException("Profile for user ID " + userId + " already exists");
		}
		
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
		
		UserProfile profile = new UserProfile();
		profile.setUser(user);
		profile.setFirstname(dto.getFirstname());
		profile.setLastname(dto.getLastname());
		profile.setPhone(dto.getPhone());
		
		AccountStatus activeStatus = accountStatusRepository.findByName(EAccountStatus.ACTIVE)
			.orElseThrow(() -> new ResourceNotFoundException("Account status", "status", EAccountStatus.ACTIVE.name()));
		user.setAccountStatus(activeStatus);
		
		userRepository.save(user);
		return userProfileRepository.save(profile);
	}
	
	
	public List<GetAllUserResponse> getAllUsersData()
	{
		return userRepository.findAllWithProfileAndStatusAndRoles().stream()
			.map(GetAllUserResponse::new)
			.toList();
	}
}
