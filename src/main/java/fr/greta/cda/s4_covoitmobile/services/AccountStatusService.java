package fr.greta.cda.s4_covoitmobile.services;

import fr.greta.cda.s4_covoitmobile.data.EAccountStatus;
import fr.greta.cda.s4_covoitmobile.models.AccountStatus;
import fr.greta.cda.s4_covoitmobile.repositories.AccountStatusRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountStatusService
{
	private final AccountStatusRepository accountStatusRepository;
	
	@Transactional
	public void initalizeStatus()
	{
		for (EAccountStatus statusEnum : EAccountStatus.values())
		{
			if (accountStatusRepository.existsByName(statusEnum))
				continue;
			
			AccountStatus status = new AccountStatus();
			status.setName(statusEnum);
			accountStatusRepository.save(status);
			log.warn("Add new status in database : {}", statusEnum);
		}
	}
}