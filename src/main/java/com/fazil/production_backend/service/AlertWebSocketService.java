package com.fazil.production_backend.service;

import com.fazil.production_backend.dto.AlertResponse;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class AlertWebSocketService {

    private final SimpMessagingTemplate messagingTemplate;

    public AlertWebSocketService(
            SimpMessagingTemplate messagingTemplate
    ) {
        this.messagingTemplate = messagingTemplate;
    }

    public void broadcastAlert(AlertResponse alert) {

        messagingTemplate.convertAndSend(
                "/topic/alerts",
                alert
        );
    }
}