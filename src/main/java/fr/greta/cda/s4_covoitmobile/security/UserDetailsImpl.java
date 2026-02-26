package fr.greta.cda.s4_covoitmobile.security;

import fr.greta.cda.s4_covoitmobile.data.EAccountStatus;
import fr.greta.cda.s4_covoitmobile.models.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Getter
public class UserDetailsImpl implements UserDetails
{
	private final transient User user;
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities()
	{
		List<SimpleGrantedAuthority> authorities = new java.util.ArrayList<>(
			user.getRoles().stream()
				.map(role -> new SimpleGrantedAuthority(role.getName().name()))
				.toList()
		);
		
		if (user.getAccountStatus() != null &&
			user.getAccountStatus().getName() == EAccountStatus.ACTIVE)
		{
			authorities.add(new SimpleGrantedAuthority("ROLE_USER_VALIDATED"));
		}
		
		return authorities;
	}
	
	@Override
	public @Nullable String getPassword()
	{
		return user.getPassword();
	}
	
	@Override
	public String getUsername()
	{
		return user.getEmail();
	}
	
	@Override
	public boolean isAccountNonExpired()
	{
		return true;
	}
	
	@Override
	public boolean isAccountNonLocked()
	{
		return true;
	}
	
	@Override
	public boolean isCredentialsNonExpired()
	{
		return true;
	}
	
	@Override
	public boolean isEnabled()
	{
		return true;
	}
	
	public Long getId() {return user.getId();}
	
	public boolean isAdmin()
	{
		return getAuthorities().stream()
			.anyMatch(a -> Objects.equals(a.getAuthority(), "ROLE_ADMIN"));
	}
	
	public boolean canAccess(Long targetId)
	{
		return this.isAdmin() || Objects.equals(this.getId(), targetId);
	}
}
