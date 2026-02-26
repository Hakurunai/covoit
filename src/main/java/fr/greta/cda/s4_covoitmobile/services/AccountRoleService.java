package fr.greta.cda.s4_covoitmobile.services;

import fr.greta.cda.s4_covoitmobile.data.EAccountRole;
import fr.greta.cda.s4_covoitmobile.models.AccountRole;
import fr.greta.cda.s4_covoitmobile.repositories.AccountRoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountRoleService
{
	private final AccountRoleRepository accountRoleRepository;
	
	@Transactional
	public void initalizeRoles()
	{
		for (EAccountRole roleEnum : EAccountRole.values())
		{
			if (accountRoleRepository.existsByName(roleEnum))
				continue;
			
			AccountRole role = new AccountRole();
			role.setName(roleEnum);
			accountRoleRepository.save(role);
			log.warn("Add new role in database : {}", roleEnum);
		}
	}
}
