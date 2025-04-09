package com.integracao_spring_hub_spot.exec.auth.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AuthServiceTest {

    private AuthService authService;
    private RestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        restTemplate = mock(RestTemplate.class);
        authService = new AuthService(restTemplate);

        // Simulando valores das @Value
        authService.clientId = "test-client-id";
        authService.clientSecret = "test-client-secret";
        authService.redirectUri = "http://localhost:8080/callback";
        authService.scopes = "contacts";
    }

    @Test
    void shouldBuildAuthUrlWithCorrectParameters() {
        String url = authService.buildAuthUrl();

        assertTrue(url.startsWith("https://app.hubspot.com/oauth/authorize"));
        assertTrue(url.contains("client_id=test-client-id"));
        assertTrue(url.contains("redirect_uri=http://localhost:8080/callback"));
        assertTrue(url.contains("scope=contacts"));
        assertTrue(url.contains("state="));
    }

    @Test
    void shouldExchangeTokenAndStoreAccessToken() {
        // Simular resposta da API da HubSpot
        Map<String, Object> tokenResponse = Map.of("access_token", "abc123");
        ResponseEntity<Map> responseEntity = new ResponseEntity<>(tokenResponse, HttpStatus.OK);

        when(restTemplate.postForEntity(
                eq("https://api.hubapi.com/oauth/v1/token"),
                any(HttpEntity.class),
                eq(Map.class)
        )).thenReturn(responseEntity);

        ResponseEntity<?> response = authService.exchangeToken("fake-code");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("abc123", AuthService.accessToken); // token estático
        assertEquals("abc123", authService.getAccessToken());
    }

    @Test
    void shouldReturnNullAccessTokenIfExchangeFails() {
        // Resposta sem body
        ResponseEntity<Map> responseEntity = new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);

        when(restTemplate.postForEntity(
                anyString(),
                any(HttpEntity.class),
                eq(Map.class)
        )).thenReturn(responseEntity);

        ResponseEntity<?> response = authService.exchangeToken("invalid-code");

        assertNull(authService.getAccessToken());
        assertEquals(HttpStatus.OK, response.getStatusCode()); // a lógica atual sempre retorna OK
    }
}