package fr.greta.cda.s4_covoitmobile.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService
{
	private final JavaMailSender mailSender;
	
	@Value("${app.mail.from}")
	private String fromEmail;
	
	@Value("${app.mail.test-recipient:default@test.com}")
	private String testRecipient;
	
	@Async
	public void sendMail(String to, String subject, String body)
	{
		SimpleMailMessage message = new SimpleMailMessage();
		
		message.setFrom(fromEmail);
		message.setTo(testRecipient);
		
		message.setSubject(subject);
		message.setText(body);
		
		mailSender.send(message);
	}
}
