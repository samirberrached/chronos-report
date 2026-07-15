package com.company.chronos.config;

import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Configures Cross-Origin Resource Sharing (CORS) for the API.
 *
 * <p>Allowed origins are externalised via the {@code chronos.cors.allowed-origins}
 * property (comma-separated) so that the same build can serve the Power BI
 * gateway, a future SPA, and local development without code changes.</p>
 */
@Configuration
@RequiredArgsConstructor
public class CorsConfig {

    @Value("${chronos.cors.allowed-origins:http://localhost:3000,http://localhost:4200}")
    private String allowedOrigins;

    /**
     * Builds the {@link CorsConfigurationSource} used by Spring Security.
     *
     * @return a URL-based CORS configuration source allowing the configured
     *         origins, standard HTTP methods and credentialed requests
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        List<String> origins = Arrays.stream(allowedOrigins.split(","))
                .map(String::trim)
                .filter(o -> !o.isBlank())
                .toList();

        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(origins);
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}