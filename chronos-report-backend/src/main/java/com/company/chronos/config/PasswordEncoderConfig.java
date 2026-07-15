package com.company.chronos.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Provides the application-wide {@link PasswordEncoder} bean.
 *
 * <p>BCrypt is used for password hashing because it incorporates a salt and is
 * deliberately slow, making brute-force attacks impractical.</p>
 */
@Configuration
public class PasswordEncoderConfig {

    /**
     * Exposes a {@link BCryptPasswordEncoder} with a strength of 12.
     *
     * @return the configured password encoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}