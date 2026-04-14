package com.corebanking.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

	private static final String BEARER_JWT = "bearer-jwt";

	@Bean
	public OpenAPI coreBankingOpenAPI() {
		return new OpenAPI()
				.components(new Components().addSecuritySchemes(BEARER_JWT,
						new SecurityScheme()
								.name(BEARER_JWT)
								.type(SecurityScheme.Type.HTTP)
								.scheme("bearer")
								.bearerFormat("JWT")));
	}
}
