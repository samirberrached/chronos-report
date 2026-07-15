package com.company.chronos.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configures the OpenAPI 3 documentation exposed by springdoc.
 *
 * <p>Declares a global "bearerAuth" security scheme so that every protected
 * endpoint in the generated Swagger UI can be exercised by supplying a JWT
 * access token. The {@code /auth/**} endpoints remain publicly callable.</p>
 */
@Configuration
public class OpenApiConfig {

    private static final String SECURITY_SCHEME_NAME = "bearerAuth";

    /**
     * Builds the {@link OpenAPI} metadata for the Chronos API.
     *
     * @return the configured OpenAPI document
     */
    @Bean
    public OpenAPI chronosOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Chronos Employee Cost Allocation API")
                        .description("REST API powering employee cost allocation reporting, "
                                + "consumed by Power BI and a future frontend.")
                        .version("v1")
                        .license(new License()
                                .name("Proprietary")
                                .url("https://company.com")))
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .components(new Components().addSecuritySchemes(SECURITY_SCHEME_NAME,
                        new SecurityScheme()
                                .name(SECURITY_SCHEME_NAME)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }
}