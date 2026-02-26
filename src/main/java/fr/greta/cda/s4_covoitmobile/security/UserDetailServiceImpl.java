package fr.greta.cda.s4_covoitmobile.security;

import fr.greta.cda.s4_covoitmobile.data.EAccountStatus;
import fr.greta.cda.s4_covoitmobile.models.User;
import fr.greta.cda.s4_covoitmobile.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserDetailServiceImpl implements UserDetailsService
{
	final UserRepository userRepository;
	
	@Override
	@Transactional
	public UserDetails loadUserByUsername(final String email) throws UsernameNotFoundException
	{
		User user = userRepository.findByEmail(email)
			.orElseThrow(() -> new UsernameNotFoundException("User not found with login : " + email));
		
		if (EAccountStatus.DELETED.equals(user.getAccountStatus().getName()))
		{
			throw new BadCredentialsException("User deleted : request unauthorized");
		}
		
		return new UserDetailsImpl(user);
	}
}
