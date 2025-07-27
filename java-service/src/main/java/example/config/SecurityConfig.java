package example.config;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.oauth2.server.resource.web.access.BearerTokenAccessDeniedHandler;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security configuration for the main application.
 *
 */
@Configuration
public class SecurityConfig {

	@Value("${jwt.public.key}")
	RSAPublicKey publicKey;

	@Value("${jwt.private.key}")
	RSAPrivateKey privateKey;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
				.authorizeHttpRequests((authorize) -> authorize
						// Needed for Swagger-UI
						.requestMatchers("/swagger-ui/**").permitAll()
						.requestMatchers("/v3/api-docs/**").permitAll()

						// Needed for H2 database console
						.requestMatchers("/h2-console/**").permitAll()

						.requestMatchers("/token").permitAll()
						.requestMatchers(HttpMethod.GET, "/notes").hasAuthority("SCOPE_READ")
						.requestMatchers(HttpMethod.POST, "/notes").hasAuthority("SCOPE_WRITE")
						.anyRequest().authenticated()
				)
				.csrf((csrf) -> csrf.ignoringRequestMatchers("/token"))
				.csrf((csrf) -> csrf.ignoringRequestMatchers("/h2-console/**"))
				// Allows iframes (H2 console)
				.headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))
				.oauth2ResourceServer((jwt) -> jwt.jwt(Customizer.withDefaults()))
				.sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.exceptionHandling((exceptions) -> exceptions
						.authenticationEntryPoint(new BearerTokenAuthenticationEntryPoint())
						.accessDeniedHandler(new BearerTokenAccessDeniedHandler())
				);

		return http.build();
	}

	@Bean
	UserDetailsService users() {
		return new InMemoryUserDetailsManager(
				User.withUsername("user1")
						.password("pwd1")
						.authorities("READ")
						.build(),
				User.withUsername("user2")
						.password("pwd2")
						.authorities("READ", "WRITE")
						.build()
		);
	}

	@Bean
	JwtDecoder jwtDecoder() {
		return NimbusJwtDecoder.withPublicKey(this.publicKey).build();
	}

	@Bean
	JwtEncoder jwtEncoder() {
		JWK jwk = new RSAKey.Builder(this.publicKey).privateKey(this.privateKey).build();
		JWKSource<SecurityContext> jwks = new ImmutableJWKSet<>(new JWKSet(jwk));
		return new NimbusJwtEncoder(jwks);
	}

}