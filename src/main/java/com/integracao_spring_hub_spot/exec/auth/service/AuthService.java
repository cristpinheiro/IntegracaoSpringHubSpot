package com.integracao_spring_hub_spot.exec.auth.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.UUID;

@Service
public class AuthService {

    @Value("${hubspot.client-id}")
    String clientId;

    @Value("${hubspot.client-secret}")
    String clientSecret;

    @Value("${hubspot.redirect-uri}")
    String redirectUri;

    @Value("${hubspot.scopes}")
    String scopes;

    private final RestTemplate restTemplate;

    public AuthService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    private static final String AUTH_URL = "https://app.hubspot.com/oauth/authorize";
    private static final String TOKEN_URL = "https://api.hubapi.com/oauth/v1/token";

    // Armazenamento temporário do token — em produção, você usaria algo persistente
    public static String accessToken;

    public String buildAuthUrl() {
        String state = UUID.randomUUID().toString();
        // ideal: salvar o state em algum armazenamento temporário para validação posterior
        return AUTH_URL +
                "?client_id=" + clientId +
                "&redirect_uri=" + redirectUri +
                "&scope=" + scopes +
                "&state=" + state;
    }

    public ResponseEntity<?> exchangeToken(String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        LinkedMultiValueMap<Object, Object> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("redirect_uri", redirectUri);
        params.add("code", code);

        HttpEntity<LinkedMultiValueMap<Object, Object>> request = new HttpEntity<>(params, headers);
        var response = restTemplate.postForEntity(TOKEN_URL, request, Map.class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            accessToken = (String) response.getBody().get("access_token");
        }

        return ResponseEntity.ok(response.getBody());
    }

    public String getAccessToken() {
        return accessToken;
    }
}
