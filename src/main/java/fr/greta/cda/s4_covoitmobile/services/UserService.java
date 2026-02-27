package fr.greta.cda.s4_covoitmobile.services;

import fr.greta.cda.s4_covoitmobile.data.EAccountRole;
import fr.greta.cda.s4_covoitmobile.data.EAccountStatus;
import fr.greta.cda.s4_covoitmobile.dto.userprofile.CreateUserProfileRequest;
import fr.greta.cda.s4_covoitmobile.dto.userprofile.UpdateProfileRequest;
import fr.greta.cda.s4_covoitmobile.exceptions.AccountAlreadyAnonymizedException;
import fr.greta.cda.s4_covoitmobile.exceptions.AlreadyExistException;
import fr.greta.cda.s4_covoitmobile.exceptions.ResourceNotFoundException;
import fr.greta.cda.s4_covoitmobile.models.AccountStatus;
import fr.greta.cda.s4_covoitmobile.models.User;
import fr.greta.cda.s4_covoitmobile.models.UserProfile;
import fr.greta.cda.s4_covoitmobile.repositories.AccountRoleRepository;
import fr.greta.cda.s4_covoitmobile.repositories.AccountStatusRepository;
import fr.greta.cda.s4_covoitmobile.repositories.UserProfileRepository;
import fr.greta.cda.s4_covoitmobile.repositories.UserRepository;
import fr.greta.cda.s4_covoitmobile.security.UserDetailsImpl;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

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
		
		User newUser = generateNewUser(mail, password, roles, status);
		return userRepository.save(newUser);
	}
	
	public boolean existByMail(String mail)
	{
		return userRepository.existsByEmail(mail);
	}
	
	@Transactional
	public UserProfile completeProfile(Long targetId, UserDetailsImpl currentUser, CreateUserProfileRequest dto)
	{
		if (targetId != null && !currentUser.canAccess(targetId))
		{
			throw new AuthorizationDeniedException("Only an admin is able to create the profile of another user");
		}
		
		Long finalTargetId = (targetId != null) ? targetId : currentUser.getId();
		if (userProfileRepository.existsById(finalTargetId))
		{
			throw new AlreadyExistException("Profile for user ID " + finalTargetId + " already exists");
		}
		
		User user = userRepository.findById(finalTargetId)
			.orElseThrow(() -> new ResourceNotFoundException("User", "id", finalTargetId));
		
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
	
	
	public List<User> getAllUsersData()
	{
		return userRepository.findAllWithProfileAndStatusAndRoles();
	}
	
	public User getUserDetail(Long userId)
	{
		return userRepository.findByIdWithProfile(userId)
			.orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
	}
	
	@Transactional
	public UserProfile patchProfile(Long targetId, UpdateProfileRequest dto)
	{
		UserProfile profile = userProfileRepository.findById(targetId)
			.orElseThrow(() -> new ResourceNotFoundException("Profile", "id", targetId));
		
		if (dto.getFirstname() != null)
		{
			profile.setFirstname(dto.getFirstname());
		}
		if (dto.getLastname() != null)
		{
			profile.setLastname(dto.getLastname());
		}
		if (dto.getPhone() != null)
		{
			profile.setPhone(dto.getPhone());
		}
		
		return profile;
	}
	
	@Transactional
	public void anonymizeUser(Long userId)
	{
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
		
		if (EAccountStatus.DELETED.equals(user.getAccountStatus().getName()))
		{
			throw new AccountAlreadyAnonymizedException("Account " + userId + " has already been anonymized");
		}
		
		AccountStatus deletedStatus = accountStatusRepository.findByName(EAccountStatus.DELETED).orElseThrow(
			() -> new ResourceNotFoundException("Deleted status", "AccountStatusRepository",
				EAccountStatus.DELETED.name())
		);
		user.setAccountStatus(deletedStatus);
		user.setEmail("deleted_" + userId + "@covoit.internal");
		user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
		
		if (user.getUserProfile() != null)
		{
			user.setUserProfile(null);
		}
	}
	
	@Transactional
	public void deleteUserByMail(String mail)
	{
		User user = userRepository.findByEmail(mail)
			.orElseThrow(() -> new ResourceNotFoundException("User", "Mail", mail));
		
		deleteUserById(user.getId());
	}
	
	@Transactional
	public void deleteUserById(Long userId)
	{
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
		
		user.getRoles().clear();
		if (user.getUserProfile() != null)
		{
			user.getUserProfile().setUser(null);
		}
		
		userRepository.delete(user);
	}
	
	/**
	 * Currently similar to {@link UserService#registerNewUser(String, String, List, EAccountStatus)}
	 * but this will shortcut any mail sending for example	 *
	 *
	 * @param mail     mail of the new user
	 * @param password password of the new user
	 * @param roles    roles given to the new user
	 * @param status   status given to the new user
	 */
	@Transactional
	public void testRegisterUser(String mail, String password, List<EAccountRole> roles, EAccountStatus status)
	{
		if (userRepository.existsByEmail(mail))
		{
			throw new AlreadyExistException("Email '" + mail + "' is already used");
		}
		
		User newUser = generateNewUser(mail, password, roles, status);
		userRepository.save(newUser);
	}
	
	
	private User generateNewUser(final String mail, final String password, final List<EAccountRole> roles,
		final EAccountStatus status)
	{
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
		
		return newUser;
	}
}
