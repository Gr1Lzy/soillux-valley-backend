package com.github.soillux.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

  @Bean
  public OpenAPI openAPI() {

    return new OpenAPI()
        .info(new Info()
            .title("Soillux Valley (Auth) API")
            .version("1.0.0")
            .description(
                """
                Authentication and authorization microservice providing
                JWT tokens with RS256 encryption.<br>
                Manages user sessions and issues cryptographically signed tokens
                for secure access to protected resources.
                """)

            .contact(new Contact()
                .name("Andrii Kolomoiets")
                .email("kolomoets02@gmail.com"))

            .license(new License()
                .name("MIT License")
                .url("https://github.com/Gr1Lzy/soillux-valley-backend?tab=MIT-1-ov-file#readme")))

        .addServersItem(new Server()
            .url("http://localhost:8080/api")
            .description("Development server"))

        .components(new Components()
            .addSecuritySchemes("bearerAuth", new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")));
  }
}
