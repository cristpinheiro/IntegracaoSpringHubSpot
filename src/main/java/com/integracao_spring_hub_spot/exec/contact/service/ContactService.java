package com.integracao_spring_hub_spot.exec.contact.service;

import com.integracao_spring_hub_spot.exec.auth.service.HubspotOAuthService;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class ContactService {

    private final RestTemplate restTemplate;
    private final HubspotOAuthService authService;

    public ContactService(RestTemplate restTemplate, HubspotOAuthService authService) {
        this.restTemplate = restTemplate;
        this.authService = authService;
    }

    public ResponseEntity<String> createContact(Map<String, Object> contactData) {
        String accessToken = authService.getAccessToken();

        if (accessToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Access token not available. Authenticate first.");
        }

        String url = "https://api.hubapi.com/crm/v3/objects/contacts";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("properties", contactData);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
        return restTemplate.postForEntity(url, request, String.class);
    }
}
