package fr.greta.cda.s4_covoitmobile;

import org.springframework.boot.SpringApplication;

public class TestS4CovoitMobileApplication
{
	
	public static void main(String[] args)
	{
		SpringApplication.from(S4CovoitMobileApplication::main).with(TestcontainersConfiguration.class).run(args);
	}
	
}
