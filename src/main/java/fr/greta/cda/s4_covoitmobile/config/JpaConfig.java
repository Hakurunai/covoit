package fr.greta.cda.s4_covoitmobile.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * This class allow us to enable an audit for our class, allowing us to automate some data injection
 */
@Configuration
@EnableJpaAuditing
public class JpaConfig
{
}