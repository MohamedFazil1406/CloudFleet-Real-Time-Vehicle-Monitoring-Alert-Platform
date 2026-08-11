package com.fazil.production_backend.controller;

import com.fazil.production_backend.dto.AlertResponse;
import com.fazil.production_backend.service.AlertService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alerts")
public class AlertController {

    private final AlertService alertService;

    public AlertController(AlertService alertService) {
        this.alertService = alertService;
    }

    // Get all alerts
    @GetMapping
    public ResponseEntity<List<AlertResponse>> getAllAlerts() {

        return ResponseEntity.ok(
                alertService.getAllAlerts()
        );
    }

    // Get alert by ID
    @GetMapping("/{id}")
    public ResponseEntity<AlertResponse> getAlert(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                alertService.getAlert(id)
        );
    }

    // Get alerts for a specific vehicle
    @GetMapping("/vehicle/{vehicleId}")
    public ResponseEntity<List<AlertResponse>> getVehicleAlerts(
            @PathVariable Long vehicleId
    ) {

        return ResponseEntity.ok(
                alertService.getVehicleAlerts(vehicleId)
        );
    }
}