package fr.greta.cda.s4_covoitmobile.eventlistener;

import fr.greta.cda.s4_covoitmobile.event.RideCanceledEvent;
import fr.greta.cda.s4_covoitmobile.services.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class RideEventListener
{
	private final MailService mailService;
	
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handleCancelRideEvent(RideCanceledEvent event)
	{
		final String subject = "Ride canceled";
		final String body = "Ride from " + event.dateTrajet() + " was canceled.";
		for (String recipient : event.recipients())
		{
			mailService.sendMail(recipient, subject, body);
		}
	}
}
