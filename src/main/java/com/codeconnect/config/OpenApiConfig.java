package com.codeconnect.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenApi() {
        return new OpenAPI()
            .info(new Info().title("Codeconnect")
                .description("Projeto para desenvolver uma rede social voltada para programadores, " +
                    "visando aprimorar minhas competências no desenvolvimento software")
                .version("1.0"));
    }

}