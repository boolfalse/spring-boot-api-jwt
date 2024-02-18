package com.microblog.api.blog.config;

import com.microblog.api.blog.security.JwtAuthenticationFilter;
import jakarta.servlet.Filter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import static com.microblog.api.blog.security.Permission.ADMIN_CREATE;
import static com.microblog.api.blog.security.Permission.ADMIN_DELETE;
import static com.microblog.api.blog.security.Permission.ADMIN_READ;
import static com.microblog.api.blog.security.Permission.ADMIN_UPDATE;
import static com.microblog.api.blog.security.Permission.MANAGER_CREATE;
import static com.microblog.api.blog.security.Permission.MANAGER_DELETE;
import static com.microblog.api.blog.security.Permission.MANAGER_READ;
import static com.microblog.api.blog.security.Permission.MANAGER_UPDATE;
import static com.microblog.api.blog.security.Role.ADMIN;
import static com.microblog.api.blog.security.Role.MANAGER;

import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;
    private final LogoutHandler logoutHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .securityMatchers( (matchers) -> matchers
            .requestMatchers("/api/v1/auth/**"))
            .authorizeHttpRequests(
                    (authorize) -> authorize.anyRequest().permitAll()
            )
            .authenticationProvider(authenticationProvider)
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}