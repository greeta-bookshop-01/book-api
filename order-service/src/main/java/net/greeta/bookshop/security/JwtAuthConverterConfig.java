package net.greeta.bookshop.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

@Configuration
public class JwtAuthConverterConfig {

    @Bean
    public JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter() {
        var jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        return jwtGrantedAuthoritiesConverter;
    }
}
