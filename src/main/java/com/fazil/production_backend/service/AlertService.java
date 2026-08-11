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

    public AlertResponse createGeofenceAlert(
            Vehicle vehicle,
            Geofence geofence,
            GeofenceEventType eventType,
            Double latitude,
            Double longitude
    ) {

        AlertType alertType =
                eventType == GeofenceEventType.ENTER
                        ? AlertType.GEOFENCE_ENTER
                        : AlertType.GEOFENCE_EXIT;

        String message =
                eventType == GeofenceEventType.ENTER
                        ? "Vehicle " + vehicle.getVehicleNumber()
                          + " entered geofence "
                          + geofence.getName()
                        : "Vehicle " + vehicle.getVehicleNumber()
                          + " exited geofence "
                          + geofence.getName();

        Alert alert = new Alert();

        alert.setVehicle(vehicle);
        alert.setGeofence(geofence);
        alert.setType(alertType);
        alert.setMessage(message);
        alert.setLatitude(latitude);
        alert.setLongitude(longitude);
        alert.setCreatedAt(LocalDateTime.now());

        Alert savedAlert = alertRepository.save(alert);

        AlertResponse response = toResponse(savedAlert);

        alertWebSocketService.broadcastAlert(response);

        return response;
    }

    public List<AlertResponse> getAllAlerts() {

        return alertRepository
                .findByOrderByCreatedAtDesc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<AlertResponse> getVehicleAlerts(
            Long vehicleId
    ) {

        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Vehicle not found: " + vehicleId
                        )
                );

        return alertRepository
                .findByVehicleOrderByCreatedAtDesc(vehicle)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public AlertResponse getAlert(Long id) {

        Alert alert = alertRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Alert not found: " + id
                        )
                );

        return toResponse(alert);
    }

    private AlertResponse toResponse(Alert alert) {

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