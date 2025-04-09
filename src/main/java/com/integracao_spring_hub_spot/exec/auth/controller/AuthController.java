package com.integracao_spring_hub_spot.exec.auth.controller;

import com.integracao_spring_hub_spot.exec.auth.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/hubspot/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping()
    public void redirectToHubspotAuth(HttpServletResponse response) throws IOException {
        String redirectUrl = authService.buildAuthUrl();
        response.sendRedirect(redirectUrl);
    }

    @GetMapping("/callback")
    public ResponseEntity<?> handleOAuthCallback(@RequestParam String code) {
        return authService.exchangeToken(code);
    }
}
