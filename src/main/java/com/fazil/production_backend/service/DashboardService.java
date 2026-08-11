package com.fazil.production_backend.service;

import com.fazil.production_backend.dto.AlertResponse;
import com.fazil.production_backend.dto.DashboardResponse;
import com.fazil.production_backend.dto.VehicleResponse;
import com.fazil.production_backend.entity.Vehicle;
import com.fazil.production_backend.enums.VehicleStatus;
import com.fazil.production_backend.repository.AlertRepository;
import com.fazil.production_backend.repository.VehicleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DashboardService {

    private final VehicleRepository vehicleRepository;
    private final AlertRepository alertRepository;

    public DashboardService(
            VehicleRepository vehicleRepository,
            AlertRepository alertRepository
    ) {
        this.vehicleRepository = vehicleRepository;
        this.alertRepository = alertRepository;
    }

    public DashboardResponse getDashboard() {

        List<Vehicle> vehicles =
                vehicleRepository.findAll();

        long totalVehicles = vehicles.size();

        long activeVehicles = vehicles.stream()
                .filter(vehicle ->
                        vehicle.getStatus() == VehicleStatus.ACTIVE
                )
                .count();

        long inactiveVehicles =
                totalVehicles - activeVehicles;

        long totalAlerts =
                alertRepository.count();

        List<VehicleResponse> vehicleResponses =
                vehicles.stream()
                        .map(this::toVehicleResponse)
                        .toList();

        List<AlertResponse> recentAlerts =
                alertRepository
                        .findByOrderByCreatedAtDesc()
                        .stream()
                        .limit(10)
                        .map(alert -> new AlertResponse(
                                alert.getId(),
                                alert.getVehicle().getId(),
                                alert.getGeofence().getId(),
                                alert.getGeofence().getName(),
                                alert.getType(),
                                alert.getMessage(),
                                alert.getLatitude(),
                                alert.getLongitude(),
                                alert.getCreatedAt()
                        ))
                        .toList();

        return new DashboardResponse(
                totalVehicles,
                activeVehicles,
                inactiveVehicles,
                totalAlerts,
                vehicleResponses,
                recentAlerts
        );
    }

    private VehicleResponse toVehicleResponse(
            Vehicle vehicle
    ) {

        return new VehicleResponse(
                vehicle.getId(),
                vehicle.getVehicleNumber(),
                vehicle.getType(),
                vehicle.getStatus(),
                vehicle.getLatitude(),
                vehicle.getLongitude(),
                vehicle.getSpeed(),
                vehicle.getLastUpdated()
        );
    }
}