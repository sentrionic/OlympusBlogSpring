package com.github.sentrionic.olympusblog.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfiguration {
    @Bean
    public GroupedOpenApi buildAPI() {
        return GroupedOpenApi.builder()
                .group("public")
                .pathsToMatch("/**")
                .build();
    }

    @Bean
    public OpenAPI getApiInfo() {
        return new OpenAPI().info(new Info()
                .title("OlympusBlog")
                .version("1.0")
                .description("API for OlympusBlog")
                .license(new License().name("Apache 2.0").url("https://springdoc.org")
                )
        );
    }
}

