package com.cognito.api.config;

import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthenticationEntryPoint authenticationEntryPoint;
    private final CognitoAuthenticationFilter cognitoAuthenticationFilter;

    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:4200"));
        configuration.setAllowedMethods(List.of("*"));
        configuration.setAllowedHeaders(List.of("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // enable cors with the above config
        http.cors(cors -> cors.configurationSource(corsConfigurationSource()));
        // disable csrf
        http.csrf(AbstractHttpConfigurer::disable);

        // HTTP Request Filter
        http.authorizeHttpRequests(
                requestMatcher -> requestMatcher
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/error/**").permitAll()
                        .anyRequest().authenticated());
         // Authentication Entry Point -> Exception Handler
        http.exceptionHandling(ex -> ex
                .authenticationEntryPoint(authenticationEntryPoint));

        // Set stateless session policy
        http.sessionManagement(
                session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // Add Cognito authentication filter
        http.addFilterBefore(cognitoAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    
        return http.build();
    }

}
