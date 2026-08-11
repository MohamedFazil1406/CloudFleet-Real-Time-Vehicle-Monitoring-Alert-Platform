package com.fazil.production_backend.service;

import com.fazil.production_backend.entity.Geofence;
import com.fazil.production_backend.entity.GeofenceEvent;
import com.fazil.production_backend.entity.Vehicle;
import com.fazil.production_backend.entity.VehicleGeofenceState;
import com.fazil.production_backend.enums.GeofenceEventType;
import com.fazil.production_backend.repository.GeofenceEventRepository;
import com.fazil.production_backend.repository.VehicleGeofenceStateRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class GeofenceEventService {

    private final VehicleGeofenceStateRepository stateRepository;
    private final GeofenceEventRepository eventRepository;
    private final GeofenceDetectionService detectionService;
    private final AlertService alertService;

    public GeofenceEventService(
            VehicleGeofenceStateRepository stateRepository,
            GeofenceEventRepository eventRepository,
            GeofenceDetectionService detectionService,
            AlertService alertService
    ) {
        this.stateRepository = stateRepository;
        this.eventRepository = eventRepository;
        this.detectionService = detectionService;
        this.alertService = alertService;
    }

    public void processLocation(
            Vehicle vehicle,
            Geofence geofence,
            Double latitude,
            Double longitude
    ) {

        boolean currentlyInside =
                detectionService.isInside(
                        geofence,
                        latitude,
                        longitude
                );

        VehicleGeofenceState state =
                stateRepository
                        .findByVehicleAndGeofence(
                                vehicle,
                                geofence
                        )
                        .orElse(null);

        // First location for this vehicle/geofence
        if (state == null) {

            state = new VehicleGeofenceState();

            state.setVehicle(vehicle);
            state.setGeofence(geofence);
            state.setInside(currentlyInside);
            state.setLastUpdated(LocalDateTime.now());

            stateRepository.save(state);

            return;
        }

        boolean previouslyInside = state.getInside();

        // No transition
        if (previouslyInside == currentlyInside) {

            state.setLastUpdated(LocalDateTime.now());

            stateRepository.save(state);

            return;
        }

        // State changed
        GeofenceEventType eventType =
                currentlyInside
                        ? GeofenceEventType.ENTER
                        : GeofenceEventType.EXIT;

        saveEvent(
                vehicle,
                geofence,
                eventType,
                latitude,
                longitude
        );

        alertService.createGeofenceAlert(
                vehicle,
                geofence,
                eventType,
                latitude,
                longitude
        );

        // Update current state
        state.setInside(currentlyInside);
        state.setLastUpdated(LocalDateTime.now());

        stateRepository.save(state);

        System.out.println(
                "GEOFENCE EVENT: " +
                        eventType +
                        " | Vehicle: " +
                        vehicle.getVehicleNumber() +
                        " | Geofence: " +
                        geofence.getName()
        );
    }

    private void saveEvent(
            Vehicle vehicle,
            Geofence geofence,
            GeofenceEventType eventType,
            Double latitude,
            Double longitude
    ) {

        GeofenceEvent event = new GeofenceEvent();

        event.setVehicle(vehicle);
        event.setGeofence(geofence);
        event.setEventType(eventType);
        event.setLatitude(latitude);
        event.setLongitude(longitude);
        event.setOccurredAt(LocalDateTime.now());

        eventRepository.save(event);
    }
}