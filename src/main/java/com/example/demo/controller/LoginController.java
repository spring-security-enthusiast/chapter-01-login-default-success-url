package com.example.demo.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class LoginController {

    @GetMapping(value = {"/auth/login"})
    public String login(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            return "redirect:/home"; // or whatever your post-login page is
        }
        return "auth/login"; // Renders the Thymeleaf template (e.g., auth/login.html)
    }
}
