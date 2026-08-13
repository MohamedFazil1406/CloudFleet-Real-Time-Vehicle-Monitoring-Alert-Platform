package com.fazil.production_backend.service;

import com.fazil.production_backend.dto.AlertResponse;
import com.fazil.production_backend.entity.Alert;
import com.fazil.production_backend.entity.Geofence;
import com.fazil.production_backend.entity.Vehicle;
import com.fazil.production_backend.enums.AlertType;
import com.fazil.production_backend.enums.GeofenceEventType;
import com.fazil.production_backend.repository.AlertRepository;
import com.fazil.production_backend.repository.VehicleRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AlertService {

    private final AlertRepository alertRepository;
    private final VehicleRepository vehicleRepository;
    private final AlertWebSocketService alertWebSocketService;

    public AlertService(
            AlertRepository alertRepository,
            VehicleRepository vehicleRepository,
            AlertWebSocketService alertWebSocketService
    ) {
        this.alertRepository = alertRepository;
        this.vehicleRepository = vehicleRepository;
        this.alertWebSocketService = alertWebSocketService;
    }

    /* ========================================================= */
    /* CREATE GEOFENCE ALERT                                      */
    /* ========================================================= */

    public AlertResponse createGeofenceAlert(
            Vehicle vehicle,
            Geofence geofence,
            GeofenceEventType eventType,
            Double latitude,
            Double longitude
    ) {

        if (vehicle == null) {
            throw new IllegalArgumentException(
                    "Vehicle is required"
            );
        }

        if (geofence == null) {
            throw new IllegalArgumentException(
                    "Geofence is required"
            );
        }

        if (eventType == null) {
            throw new IllegalArgumentException(
                    "Geofence event type is required"
            );
        }

        if (latitude == null || longitude == null) {
            throw new IllegalArgumentException(
                    "Vehicle location is required"
            );
        }

        /*
         * Convert ENTER / EXIT event into
         * frontend AlertType.
         */
        AlertType alertType;

        if (eventType == GeofenceEventType.ENTER) {

            alertType =
                    AlertType.GEOFENCE_ENTER;

        } else if (
                eventType == GeofenceEventType.EXIT
        ) {

            alertType =
                    AlertType.GEOFENCE_EXIT;

        } else {

            throw new IllegalArgumentException(
                    "Unsupported geofence event type: "
                            + eventType
            );
        }

        /* ===================================================== */
        /* MESSAGE                                                */
        /* ===================================================== */

        String message;

        if (
                eventType == GeofenceEventType.ENTER
        ) {

            message =
                    "Vehicle "
                            + vehicle.getVehicleNumber()
                            + " entered geofence "
                            + geofence.getName();

        } else {

            message =
                    "Vehicle "
                            + vehicle.getVehicleNumber()
                            + " exited geofence "
                            + geofence.getName();
        }

        /* ===================================================== */
        /* CREATE ALERT                                           */
        /* ===================================================== */

        Alert alert =
                new Alert();

        alert.setVehicle(
                vehicle
        );

        alert.setGeofence(
                geofence
        );

        alert.setType(
                alertType
        );

        alert.setMessage(
                message
        );

        alert.setLatitude(
                latitude
        );

        alert.setLongitude(
                longitude
        );

        alert.setCreatedAt(
                LocalDateTime.now()
        );

        /* ===================================================== */
        /* SAVE                                                    */
        /* ===================================================== */

        Alert savedAlert =
                alertRepository.save(
                        alert
                );

        AlertResponse response =
                toResponse(
                        savedAlert
                );

        /* ===================================================== */
        /* REAL-TIME BROADCAST                                     */
        /* ===================================================== */

        try {

            alertWebSocketService.broadcastAlert(
                    response
            );

        } catch (Exception exception) {

            /*
             * The alert is already saved.
             *
             * A WebSocket problem should not cause
             * the vehicle/geofence transaction to fail.
             */
            System.err.println(
                    "Failed to broadcast alert: "
                            + exception.getMessage()
            );
        }

        return response;
    }

    /* ========================================================= */
    /* GET ALL ALERTS                                             */
    /* ========================================================= */

    public List<AlertResponse> getAllAlerts() {

        return alertRepository
                .findByOrderByCreatedAtDesc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    /* ========================================================= */
    /* GET VEHICLE ALERTS                                         */
    /* ========================================================= */

    public List<AlertResponse> getVehicleAlerts(
            Long vehicleId
    ) {

        Vehicle vehicle =
                vehicleRepository
                        .findById(vehicleId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Vehicle not found: "
                                                + vehicleId
                                )
                        );

        return alertRepository
                .findByVehicleOrderByCreatedAtDesc(
                        vehicle
                )
                .stream()
                .map(this::toResponse)
                .toList();
    }

    /* ========================================================= */
    /* GET ALERT                                                  */
    /* ========================================================= */

    public AlertResponse getAlert(
            Long id
    ) {

        Alert alert =
                alertRepository
                        .findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Alert not found: "
                                                + id
                                )
                        );

        return toResponse(
                alert
        );
    }

    /* ========================================================= */
    /* RESPONSE MAPPER                                            */
    /* ========================================================= */

    private AlertResponse toResponse(
            Alert alert
    ) {

        return new AlertResponse(
                alert.getId(),
                alert.getVehicle().getId(),
                alert.getGeofence().getId(),
                alert.getGeofence().getName(),
                alert.getType(),
                alert.getMessage(),
                alert.getLatitude(),
                alert.getLongitude(),
                alert.getCreatedAt()
        );
    }
}