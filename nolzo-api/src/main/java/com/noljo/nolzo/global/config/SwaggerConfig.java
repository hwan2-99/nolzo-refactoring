package com.noljo.nolzo.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

  @Bean
  public OpenAPI openAPI() {
    io.swagger.v3.oas.models.security.SecurityScheme securityScheme = getSecurityScheme();
    io.swagger.v3.oas.models.security.SecurityRequirement securityRequirement = getSecurityRequireMent();

    return new OpenAPI()
            .info(new Info()
                    .title("DeepGround Server API")
                    .description("DeepGround APIs")
                    .version("1.0.0"))
            .components(new Components().addSecuritySchemes("bearerAuth", securityScheme))
            .security(java.util.List.of(securityRequirement));
  }

  private io.swagger.v3.oas.models.security.SecurityScheme getSecurityScheme() {
    return new io.swagger.v3.oas.models.security.SecurityScheme().type(io.swagger.v3.oas.models.security.SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")
            .in(SecurityScheme.In.HEADER).name("Authorization");
  }

  private io.swagger.v3.oas.models.security.SecurityRequirement getSecurityRequireMent() {
    return new SecurityRequirement().addList("bearerAuth");
  }
}
