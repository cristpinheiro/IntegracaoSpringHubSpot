package com.integracao_spring_hub_spot.exec.auth.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/hubspot/oauth")
public class AuthController {
    private final String CLIENT_ID = "d8654166-653a-44c3-a1ce-e8e4e81ba825";
    private final String CLIENT_SECRET = "b1962a92-e937-4529-88f9-8d7c06d30126";
    private final String REDIRECT_URI = "http://localhost:8080/hubspot/oauth/callback";
    private final String SCOPES = "crm.objects.contacts.write oauth crm.schemas.contacts.read";
    public static String accessToken;

    public AuthController(@Lazy RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    private final RestTemplate restTemplate;

    @GetMapping("/auth")
    public void redirectToHubspotAuth(HttpServletResponse response) throws IOException {
        String state = UUID.randomUUID().toString();
        String authUrl = "https://app.hubspot.com/oauth/authorize"
                + "?client_id=" + CLIENT_ID
                + "&redirect_uri=" + REDIRECT_URI
                + "&scope=" + SCOPES
                + "&state=" + state;

        response.sendRedirect(authUrl);
    }

    @GetMapping("/callback")
    public ResponseEntity<String> handleOAuthCallback(@RequestParam String code) {
        String tokenUrl = "https://api.hubapi.com/oauth/v1/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        LinkedMultiValueMap<Object, Object> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", CLIENT_ID);
        params.add("client_secret", CLIENT_SECRET);
        params.add("redirect_uri", REDIRECT_URI);
        params.add("code", code);

        HttpEntity<LinkedMultiValueMap<Object, Object>> request = new HttpEntity<>(params, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(tokenUrl, request, Map.class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            accessToken = (String) response.getBody().get("access_token");
        }

        return ResponseEntity.ok(response.getBody().toString());
    }
}
