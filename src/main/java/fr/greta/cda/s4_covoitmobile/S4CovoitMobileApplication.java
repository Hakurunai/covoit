package fr.greta.cda.s4_covoitmobile;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class S4CovoitMobileApplication
{
	public static void main(String[] args)
	{
		SpringApplication.run(S4CovoitMobileApplication.class, args);
	}
}
