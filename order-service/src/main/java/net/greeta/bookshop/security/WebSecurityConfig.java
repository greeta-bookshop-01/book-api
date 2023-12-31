package net.greeta.bookshop.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.savedrequest.NoOpServerRequestCache;

@RequiredArgsConstructor
@Configuration
@EnableWebFluxSecurity
public class WebSecurityConfig {

    private final JwtAuthConverter jwtAuthConverter;

    @Bean
    SecurityWebFilterChain filterChain(ServerHttpSecurity http) {
        return http
                .authorizeExchange(exchange -> exchange

                        .pathMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs", "/v3/api-docs/**").permitAll()

                        .pathMatchers(HttpMethod.GET,"/", "/**").permitAll()

                        .pathMatchers("/", "/**").hasRole(BOOK_USER)

                        .anyExchange().authenticated())
                .oauth2ResourceServer(oauth2ResourceServer -> oauth2ResourceServer.jwt(
                        jwt -> jwt.jwtAuthenticationConverter(new ReactiveJwtAuthenticationConverterAdapter(jwtAuthConverter))))
                .requestCache(requestCacheSpec ->
                        requestCacheSpec.requestCache(NoOpServerRequestCache.getInstance()))
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .build();
    }

    public static final String BOOK_MANAGER = "BOOK_MANAGER";
    public static final String BOOK_USER = "BOOK_USER";
}