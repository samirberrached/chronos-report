package com.company.chronos.security;

import com.company.chronos.util.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

/**
 * Central Spring Security configuration.
 *
 * <p>Configures a fully stateless, JWT-based security model: no sessions, no
 * CSRF, a {@link JwtAuthenticationFilter} before the username/password filter,
 * and role-based authorization. The {@code /auth/**} endpoints are public;
 * everything else requires a valid JWT. Swagger UI is optionally public per
 * profile.</p>
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    /** JWT authentication filter. */
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /** CORS configuration source. */
    private final CorsConfigurationSource corsConfigurationSource;

    /** Whether Swagger UI is publicly reachable (local/docker profiles). */
    @Value("${chronos.security.swagger-public:false}")
    private boolean swaggerPublic;

    /**
     * Exposes the {@link AuthenticationManager} built from the authentication
     * configuration.
     *
     * @param authenticationConfiguration the Spring authentication config
     * @return the authentication manager
     * @throws Exception if the manager cannot be resolved
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * Builds the security filter chain.
     *
     * @param http the http security builder
     * @param passwordEncoder the password encoder (unused directly here but
     *                         required to ensure the bean is created)
     * @return the configured filter chain
     * @throws Exception if configuration fails
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers(Constants.AUTH_BASE + "/**").permitAll();
                    if (swaggerPublic) {
                        auth.requestMatchers(
                                        "/swagger-ui/**",
                                        "/swagger-ui.html",
                                        "/v3/api-docs/**",
                                        "/api-docs/**").permitAll();
                    }
                    auth.requestMatchers("/actuator/health").permitAll();
                    auth.anyRequest().authenticated();
                })
                .addFilterBefore(jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}