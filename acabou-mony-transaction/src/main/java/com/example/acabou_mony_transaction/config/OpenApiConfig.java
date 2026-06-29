package com.example.acabou_mony_transaction.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI configuration for Springdoc
 * Provides API documentation via Swagger UI at /swagger-ui.html
 */
@Configuration
public class OpenApiConfig {

    /**
     * Configures OpenAPI documentation
     * * @return OpenAPI bean with customized title, description, version, and JWT Auth
     */
    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                // 1. Informações Gerais da API
                .info(new Info()
                        .title("Acabou-Mony Transaction Service API")
                        .version("1.0.0")
                        .description("API for processing financial transactions including transfers, debits, and credits. " +
                                "Provides transaction management, balance verification, and audit logging.")
                        .contact(new Contact()
                                .name("Acabou-Mony Development Team")
                                .email("dev@acabou-mony.com")
                                .url("https://github.com/acabou-mony"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
                // 2. Configuração de Servidores
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8083")
                                .description("Local Development Server"),
                        new Server()
                                .url("http://localhost:8088/api/transactions")
                                .description("Production Server (via API Gateway)")))
                // 3. Adição do Botão "Authorize" Globalmente
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                // 4. Configuração do Componente JWT Bearer
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }
}