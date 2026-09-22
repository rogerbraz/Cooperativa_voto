package com.cooperativa.votacao.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API de Votação em Cooperativas (Padrão MVC)")
                        .description("API REST para gerenciamento de pautas, sessões de votação e Server-Driven UI para dispositivos móveis.")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("Equipe de Engenharia")
                                .email("dev@cooperativa.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://springdoc.org")));
    }
}
