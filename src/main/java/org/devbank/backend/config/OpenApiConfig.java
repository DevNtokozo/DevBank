package org.devbank.backend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI devBankOpenAPI() {

        return new OpenAPI()
                .info(new Info()
                        .title("DevBank API")
                        .version("1.0")
                        .description(
                                "REST API for the DevBank banking application. "
                                        + "Provides authentication, account management, "
                                        + "transfers, transactions and beneficiary management."
                        ));
    }
}