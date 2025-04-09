package com.integracao_spring_hub_spot.exec.contact.service;

import com.integracao_spring_hub_spot.exec.auth.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class ContactServiceTest {

    private RestTemplate restTemplate;
    private AuthService authService;
    private ContactService contactService;

    @BeforeEach
    void setup() {
        restTemplate = mock(RestTemplate.class);
        authService = mock(AuthService.class);
        contactService = new ContactService(restTemplate, authService);
    }

    @Test
    void shouldReturnUnauthorizedWhenAccessTokenIsNull() {
        when(authService.getAccessToken()).thenReturn(null);

        ResponseEntity<String> response = contactService.createContact(new HashMap<>());

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Access token not available. Authenticate first.", response.getBody());
    }

    @Test
    void shouldCreateContactSuccessfullyWhenTokenIsValid() {
        String fakeToken = "abc123";
        when(authService.getAccessToken()).thenReturn(fakeToken);

        Map<String, Object> contactData = Map.of("email", "test@example.com");
        ResponseEntity<String> expectedResponse = new ResponseEntity<>("Contato criado", HttpStatus.CREATED);

        when(restTemplate.postForEntity(
                eq("https://api.hubapi.com/crm/v3/objects/contacts"),
                any(HttpEntity.class),
                eq(String.class)
        )).thenReturn(expectedResponse);

        ResponseEntity<String> response = contactService.createContact(contactData);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Contato criado", response.getBody());

        var captor = ArgumentCaptor.forClass(HttpEntity.class);
        verify(restTemplate).postForEntity(eq("https://api.hubapi.com/crm/v3/objects/contacts"), captor.capture(), eq(String.class));

        var capturedRequest = captor.getValue();
        String authHeader = capturedRequest.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        assertNotNull(authHeader);
        assertTrue(authHeader.startsWith("Bearer "));
    }
}