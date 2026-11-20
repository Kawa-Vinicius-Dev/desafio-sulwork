package com.sulwork.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI sulworkOpenApi() {
        return new OpenAPI().info(new Info()
                .title("Sulwork - Eventos de Alimentação")
                .description("API para organizar eventos, participantes, itens e contribuições.")
                .version("v1"));
    }
}