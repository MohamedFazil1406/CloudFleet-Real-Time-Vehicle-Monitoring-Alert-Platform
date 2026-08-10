package com.fazil.production_backend.service;

import com.fazil.production_backend.dto.LocationRequest;
import com.fazil.production_backend.dto.LocationResponse;
import com.fazil.production_backend.entity.Vehicle;
import com.fazil.production_backend.entity.VehicleLocation;
import com.fazil.production_backend.repository.VehicleLocationRepository;
import com.fazil.production_backend.repository.VehicleRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class LocationService {

    private final VehicleRepository vehicleRepository;
    private final VehicleLocationRepository locationRepository;

    public LocationService(
            VehicleRepository vehicleRepository,
            VehicleLocationRepository locationRepository
    ) {
        this.vehicleRepository = vehicleRepository;
        this.locationRepository = locationRepository;
    }

    public LocationResponse updateLocation(
            Long vehicleId,
            LocationRequest request
    ) {

        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Vehicle not found: " + vehicleId
                        )
                );

        VehicleLocation location = new VehicleLocation();

        location.setVehicle(vehicle);
        location.setLatitude(request.getLatitude());
        location.setLongitude(request.getLongitude());
        location.setSpeed(request.getSpeed());
        location.setRecordedAt(LocalDateTime.now());

        VehicleLocation savedLocation =
                locationRepository.save(location);

        // Update vehicle's current location
        vehicle.setLatitude(request.getLatitude());
        vehicle.setLongitude(request.getLongitude());
        vehicle.setSpeed(request.getSpeed());
        vehicle.setLastUpdated(LocalDateTime.now());

        vehicleRepository.save(vehicle);

        return toResponse(savedLocation);
    }

    public List<LocationResponse> getLocationHistory(
            Long vehicleId
    ) {

        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Vehicle not found: " + vehicleId
                        )
                );

        return locationRepository
                .findByVehicleOrderByRecordedAtDesc(vehicle)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private LocationResponse toResponse(
            VehicleLocation location
    ) {

        return new LocationResponse(
                location.getId(),
                location.getVehicle().getId(),
                location.getLatitude(),
                location.getLongitude(),
                location.getSpeed(),
                location.getRecordedAt()
        );
    }
}