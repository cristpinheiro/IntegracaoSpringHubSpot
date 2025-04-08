package com.integracao_spring_hub_spot.exec.contact.controller;

import com.integracao_spring_hub_spot.exec.contact.service.ContactService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/hubspot")
public class ContactController {

    private final ContactService contactService;

    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    @PostMapping("/contacts")
    public ResponseEntity<String> createContact(@RequestBody Map<String, Object> contactData) {
        return contactService.createContact(contactData);
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