package edu.icet.ecom.config;

import edu.icet.ecom.util.SecurityUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class AuditConfig {

    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> {
            try {
                // We use your exact Zero-Trust utility to get the email!
                return Optional.of(SecurityUtil.getCurrentUserEmail());
            } catch (Exception e) {
                return Optional.of("SYSTEM"); // Fallback for auto-generated events
            }
        };
    }
}