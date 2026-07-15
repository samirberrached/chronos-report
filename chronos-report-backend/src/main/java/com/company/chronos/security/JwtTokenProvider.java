package com.company.chronos.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

/**
 * Stateless JWT provider responsible for issuing and validating access and
 * refresh tokens.
 *
 * <p>Tokens are signed with HMAC-SHA using the configured secret. The access
 * token carries the subject (email) and role; the refresh token carries only
 * the subject.</p>
 */
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    /** Secret key material used to sign tokens. */
    @Value("${chronos.jwt.secret}")
    private String secret;

    /** Access token lifetime in milliseconds. */
    @Value("${chronos.jwt.access-token-expiration-ms}")
    private long accessTokenExpirationMs;

    /** Refresh token lifetime in milliseconds. */
    @Value("${chronos.jwt.refresh-token-expiration-ms}")
    private long refreshTokenExpirationMs;

    /** Claim key under which the role is stored. */
    private static final String ROLE_CLAIM = "role";

    /**
     * Builds the signing key from the configured secret.
     *
     * @return the HMAC secret key
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Generates a short-lived access token for the given user.
     *
     * @param userDetails the authenticated principal
     * @param role the application role to embed
     * @return the signed access token
     */
    public String generateAccessToken(UserDetails userDetails, String role) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + accessTokenExpirationMs);
        return Jwts.builder()
                .subject(userDetails.getUsername())
                .claim(ROLE_CLAIM, role)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Generates a long-lived refresh token for the given user.
     *
     * @param userDetails the authenticated principal
     * @return the signed refresh token
     */
    public String generateRefreshToken(UserDetails userDetails) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + refreshTokenExpirationMs);
        return Jwts.builder()
                .subject(userDetails.getUsername())
                .issuedAt(now)
                .expiration(expiry)
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Returns the username (subject) stored in the token.
     *
     * @param token the JWT
     * @return the subject username
     */
    public String getUsername(String token) {
        return parseClaims(token).getSubject();
    }

    /**
     * Returns the role claim stored in the token.
     *
     * @param token the JWT
     * @return the role string
     */
    public String getRole(String token) {
        return parseClaims(token).get(ROLE_CLAIM, String.class);
    }

    /**
     * Validates the token signature and expiry.
     *
     * @param token the JWT
     * @return true if the token is well-formed and not expired
     */
    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * Parses the claims of a token, throwing if invalid.
     *
     * @param token the JWT
     * @return the parsed claims
     */
    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Returns the configured access token lifetime.
     *
     * @return lifetime in milliseconds
     */
    public long getAccessTokenExpirationMs() {
        return accessTokenExpirationMs;
    }

    /**
     * Builds a Spring Security authority list from a role string.
     *
     * @param role the application role
     * @return the authority list
     */
    public List<GrantedAuthority> toAuthorities(String role) {
        return List.of(new org.springframework.security.core.authority
                .SimpleGrantedAuthority("ROLE_" + role));
    }
}