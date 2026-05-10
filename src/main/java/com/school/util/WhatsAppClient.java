package com.school.util;

import com.school.exception.ValidationException;
import com.school.utilenum.NotificationStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@Slf4j
public class WhatsAppClient {

    private final RestTemplate restTemplate;

    public WhatsAppClient() {
        this.restTemplate = new RestTemplate();
    }

    public NotificationStatus sendMessage(String phone, String message) {
        // Mock implementation for WhatsApp API integration
        log.info("Sending WhatsApp message to {}: {}", phone, message);
        if (phone == null || !phone.matches("\\d{10}")) {
            throw new ValidationException("Invalid phone number");
        }

        try {
            // TODO: Replace with actual WhatsApp API call
            // Example: restTemplate.postForObject("https://api.whatsapp.com/send", request, Response.class);

            // For now, simulate success
            return NotificationStatus.SUCCESS;
        } catch (Exception e) {
            log.error("Failed to send WhatsApp message", e);
            return NotificationStatus.FAILED;
        }
    }
}