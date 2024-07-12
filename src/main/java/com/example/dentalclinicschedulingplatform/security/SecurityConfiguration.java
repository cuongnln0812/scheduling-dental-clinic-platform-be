package com.example.dentalclinicschedulingplatform.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity

public class SecurityConfiguration {

        private final JwtAuthenticationFilter jwtAuthFilter;

        private final AuthenticationProvider authenticationProvider;

        private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http.csrf(AbstractHttpConfigurer::disable)
                                .authorizeHttpRequests(authorize -> authorize
                                                .requestMatchers(
                                                        //"/api/v1/staff/**",
                                                                "/api/v1/auth/login/**",
                                                                "/api/v1/auth/register",
                                                                "/api/v1/dentists",
                                                                "/api/v1/dentists/{dentistId}",
                                                                "/v2/api-docs",
                                                                "/v3/api-docs",
                                                                "/v3/api-docs/**",
                                                                "/swagger-resources",
                                                                "/swagger-resources/**",
                                                                "/configuration/ui",
                                                                "/configuration/security",
                                                                "/swagger-ui.html",
                                                                "/webjars/**",
                                                                "/swagger-ui/**",
                                                        "/api/v1/clinics/registration",
                                                        "/api/v1/staff/**",
                                                        "/api/v1/branch/**",
                                                        "/api/v1/blog/**",
                                                        "/api/v1/service",
                                                        "api/v1/category",
                                                        "/api/v1/slot",
                                                        "/api/v1/feedback/branch/{branchId}",
                                                        "/api/v1/feedback/all",
                                                        "/api/v1/slot/available",
                                                        "/api/v1/slot/available-by-date",
                                                        "/api/v1/working-hours/{clinicId}",
                                                        "/api/v1/auth/refresh-token",
                                                        "/api/v1/auth/logout",
                                                        "/api/v1/auth/login-google",
                                                        "api/v1/clinics/{clinicId}",
                                                        "api/v1/clinics",
                                                "api/v1/dentists/clinic/{clinicId}")
                                                .permitAll()
                                                .requestMatchers(
                                                        "/api/v1/auth/user-information",
                                                        "/api/v1/auth/change-password",
                                                        "/api/v1/auth/homepage-statistics",
                                                        "/api/v1/output-standard/**")
                                                .authenticated()
                                                .anyRequest().authenticated())
                                .exceptionHandling(exception -> exception
                                                .authenticationEntryPoint(this.jwtAuthenticationEntryPoint))
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .authenticationProvider(this.authenticationProvider)
                                .addFilterBefore(this.jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

                // http.csrf(AbstractHttpConfigurer::disable);
                return http.build();
        }

}
