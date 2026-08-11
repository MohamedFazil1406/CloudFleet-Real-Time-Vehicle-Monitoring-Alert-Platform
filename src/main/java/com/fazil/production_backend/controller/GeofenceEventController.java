package com.fazil.production_backend.controller;

import com.fazil.production_backend.dto.GeofenceEventResponse;
import com.fazil.production_backend.entity.GeofenceEvent;
import com.fazil.production_backend.entity.Vehicle;
import com.fazil.production_backend.repository.GeofenceEventRepository;
import com.fazil.production_backend.repository.VehicleRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class GeofenceEventController {

    private final GeofenceEventRepository eventRepository;
    private final VehicleRepository vehicleRepository;

    public GeofenceEventController(
            GeofenceEventRepository eventRepository,
            VehicleRepository vehicleRepository
    ) {
        this.eventRepository = eventRepository;
        this.vehicleRepository = vehicleRepository;
    }

    @GetMapping("/vehicles/{vehicleId}/geofence-events")
    public ResponseEntity<List<GeofenceEventResponse>> getVehicleEvents(
            @PathVariable Long vehicleId
    ) {

        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Vehicle not found: " + vehicleId
                        )
                );

        List<GeofenceEventResponse> events =
                eventRepository
                        .findByVehicleOrderByOccurredAtDesc(vehicle)
                        .stream()
                        .map(this::toResponse)
                        .toList();

        return ResponseEntity.ok(events);
    }

    @GetMapping("/geofence-events")
    public ResponseEntity<List<GeofenceEventResponse>> getAllEvents() {

        List<GeofenceEventResponse> events =
                eventRepository.findAll()
                        .stream()
                        .map(this::toResponse)
                        .toList();

        return ResponseEntity.ok(events);
    }

    private GeofenceEventResponse toResponse(
            GeofenceEvent event
    ) {

        return new GeofenceEventResponse(
                event.getId(),
                event.getVehicle().getId(),
                event.getGeofence().getId(),
                event.getGeofence().getName(),
                event.getEventType(),
                event.getLatitude(),
                event.getLongitude(),
                event.getOccurredAt()
        );
    }
}