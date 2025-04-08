package com.integracao_spring_hub_spot.exec.contact.controller;

import com.integracao_spring_hub_spot.exec.auth.controller.AuthController;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/hubspot")
class ContactController {

    private final RestTemplate restTemplate;

    public ContactController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @PostMapping("/contacts")
    public ResponseEntity<String> createContact(@RequestBody Map<String, Object> contactData) {
        if (AuthController.accessToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Access token not available. Authenticate first.");
        }

        String url = "https://api.hubapi.com/crm/v3/objects/contacts";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(AuthController.accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("properties", contactData);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

        return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(@RequestBody List<Map<String, Object>> events) {
        for (Map<String, Object> event : events) {
            System.out.println("🔔 Webhook recebido: " + event);
            String eventType = (String) event.get("subscriptionType");
            if ("contact.creation".equals(eventType)) {
                System.out.println("➡️ Novo contato criado! ID: " + event.get("objectId"));
            }
        }
        return ResponseEntity.ok("Webhook recebido com sucesso.");
    }
}
