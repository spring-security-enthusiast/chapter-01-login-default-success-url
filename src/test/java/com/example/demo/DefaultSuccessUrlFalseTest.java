package com.example.demo;

import com.example.demo.config.UserConfig;
import com.example.demo.controller.LoginController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.security.autoconfigure.web.servlet.PathRequest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LoginController.class)
@AutoConfigureMockMvc
@Import({
        DefaultSuccessUrlFalseTest.TestSecurityConfig.class,
        UserConfig.class
})
public class DefaultSuccessUrlFalseTest {

    @Autowired
    MockMvc mvc;

    @Test
    @DisplayName("Test redirect the user back to the original URL")
    void defaultSuccessUrlTestBooleanFalse() throws Exception {

        mvc.perform(MockMvcRequestBuilders.get("/about"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/auth/login"))
            .andDo(result -> {
                var session = result.getRequest().getSession(false);
                mvc.perform(MockMvcRequestBuilders.post("/auth/login_processing")
                    .param("username", "admin")
                    .param("password", "password")
                    .with(csrf())
                    .session((MockHttpSession) session))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("http://localhost/about?continue"));
        });
    }

    @TestConfiguration
    @EnableWebSecurity
    static class TestSecurityConfig {

        @Bean
        public SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
            http
                .csrf(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> auth
                    .requestMatchers(
                        PathRequest.toStaticResources().atCommonLocations(),
                        PathPatternRequestMatcher.withDefaults().matcher("/auth/login"),
                        PathPatternRequestMatcher.withDefaults().matcher("/error/**")
                    ).permitAll()
                    .anyRequest().authenticated()
                ).formLogin(form -> form
                        .loginPage("/auth/login")
                        .loginProcessingUrl("/auth/login_processing")
                        .defaultSuccessUrl("/home", false)
                        .permitAll()
                );
            return http.build();
        }
    }
}
