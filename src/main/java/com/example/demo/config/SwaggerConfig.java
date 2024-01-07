package com.example.demo.config;


import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class SwaggerConfig {


    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI().addSecurityItem(new SecurityRequirement().
                        addList("Authentication Bearer"))
                .components(new Components().addSecuritySchemes
                        ("Authentication Bearer", createAPIKeyScheme()))
                .info(new Info().title("KKang API")
                        .description("this is KKang API.")
                        .version("1.0").contact(new Contact().name("KKang sample")
                                .email( "www.sample.com").url("sample@gmail.com"))
                        .license(new License().name("License of API")
                                .url("API license URL")));
    }

    private SecurityScheme createAPIKeyScheme() {
        return new SecurityScheme().type(SecurityScheme.Type.HTTP)
                .name("Authorization")
                .scheme("Bearer")
                .bearerFormat("JWT");
    }



}