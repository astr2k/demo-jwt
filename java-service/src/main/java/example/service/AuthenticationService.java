package example.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Authentication Service.
 */
@Component
public class AuthenticationService {

    private final Long expirationInSeconds;
    private final JwtEncoder encoder;
    private final UserDetailsService userDetailsService;

    public AuthenticationService(@Value("${jwt.expiration.seconds:300}") Long expirationInSeconds,
                                 JwtEncoder encoder,
                                 UserDetailsService userDetailsService) {
        this.expirationInSeconds = expirationInSeconds;
        this.encoder = encoder;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Authenticates user by username, password.
     *
     * @param username
     * @param password
     * @return JWT token
     */
    public String authenticate(String username, String password) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        if (Objects.equals(password, userDetails.getPassword())) {
            String scopes = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.joining(" "));
            Instant now = Instant.now();
            JwtClaimsSet claims = JwtClaimsSet.builder()
                    .issuer("self")
                    .issuedAt(now)
                    .expiresAt(now.plusSeconds(expirationInSeconds))
                    .subject(username)
                    .claim("scope", scopes)
                    .build();
            return this.encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
        }

        throw new BadCredentialsException("Bad credentials");
    }
}
