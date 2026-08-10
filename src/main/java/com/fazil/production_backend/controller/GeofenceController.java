package com.fazil.production_backend.controller;

import com.fazil.production_backend.dto.GeofenceRequest;
import com.fazil.production_backend.dto.GeofenceResponse;
import com.fazil.production_backend.service.GeofenceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/geofences")
public class GeofenceController {

    private final GeofenceService geofenceService;

    public GeofenceController(
            GeofenceService geofenceService
    ) {
        this.geofenceService = geofenceService;
    }

    @PostMapping
    public ResponseEntity<GeofenceResponse> createGeofence(
            @RequestBody GeofenceRequest request
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(geofenceService.createGeofence(request));
    }

    @GetMapping
    public ResponseEntity<List<GeofenceResponse>> getAllGeofences() {

        return ResponseEntity.ok(
                geofenceService.getAllGeofences()
        );
    }

    @GetMapping("/active")
    public ResponseEntity<List<GeofenceResponse>> getActiveGeofences() {

        return ResponseEntity.ok(
                geofenceService.getActiveGeofences()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<GeofenceResponse> getGeofence(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                geofenceService.getGeofence(id)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<GeofenceResponse> updateGeofence(
            @PathVariable Long id,
            @RequestBody GeofenceRequest request
    ) {

        return ResponseEntity.ok(
                geofenceService.updateGeofence(id, request)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGeofence(
            @PathVariable Long id
    ) {

        geofenceService.deleteGeofence(id);

        return ResponseEntity.noContent().build();
    }
}