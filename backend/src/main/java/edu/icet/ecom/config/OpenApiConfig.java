package edu.icet.ecom.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Enterprise Car Management API",
                version = "1.0",
                description = "RESTful API for Car Rentals, featuring JWT Security, Pagination, Async Events, and Zero-Trust Architecture.",
                contact = @Contact(
                        name = "Lead Architect (You)",
                        email = "architect@carrentals.com"
                )
        ),
        // This tells Swagger that every endpoint needs a token by default
        security = {
                @SecurityRequirement(name = "bearerAuth")
        }
)
@SecurityScheme(
        name = "bearerAuth",
        description = "JWT Auth validation. Please log in to get your token, then paste it here.",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {
}