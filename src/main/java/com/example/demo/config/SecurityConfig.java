package com.example.demo.config;

import org.springframework.boot.security.autoconfigure.web.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;

@Configuration
@EnableWebSecurity(debug = true)
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(Customizer.withDefaults()) // CSRF protection configuration
            .authorizeHttpRequests(authorize -> authorize
                    .requestMatchers(
                        PathRequest.toStaticResources().atCommonLocations(),
                        PathPatternRequestMatcher.withDefaults().matcher("/error/**")
                    ).permitAll()
                    .requestMatchers("/auth/login").permitAll()
                    .anyRequest().authenticated()
            )
            .formLogin(formLogin -> formLogin
                .loginPage("/auth/login")          // GET: show the login page
                .loginProcessingUrl("/auth/login") // POST: process the login form
                .permitAll()                       // also whitelists /auth/login (GET + POST)
            );
            // no .logout(...) here yet

        return http.build();
    }
}
