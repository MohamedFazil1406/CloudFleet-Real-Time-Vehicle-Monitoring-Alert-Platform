package com.fazil.production_backend.service;

import com.fazil.production_backend.dto.LocationRequest;
import com.fazil.production_backend.dto.LocationResponse;
import com.fazil.production_backend.entity.Geofence;
import com.fazil.production_backend.entity.Vehicle;
import com.fazil.production_backend.entity.VehicleLocation;
import com.fazil.production_backend.repository.GeofenceRepository;
import com.fazil.production_backend.repository.VehicleLocationRepository;
import com.fazil.production_backend.repository.VehicleRepository;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class LocationService {

    private final VehicleRepository vehicleRepository;
    private final VehicleLocationRepository locationRepository;
    private final GeofenceRepository geofenceRepository;
    private final GeofenceEventService geofenceEventService;

    public LocationService(
            VehicleRepository vehicleRepository,
            VehicleLocationRepository locationRepository,
            GeofenceRepository geofenceRepository,
            GeofenceEventService geofenceEventService
    ) {
        this.vehicleRepository = vehicleRepository;
        this.locationRepository = locationRepository;
        this.geofenceRepository = geofenceRepository;
        this.geofenceEventService = geofenceEventService;
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

        validateLocation(request);

        // Save location history
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

        // Check active geofences
        checkGeofences(
                vehicle,
                request.getLatitude(),
                request.getLongitude()
        );

        return toResponse(savedLocation);
    }

    private void checkGeofences(
            Vehicle vehicle,
            Double latitude,
            Double longitude
    ) {

        List<Geofence> activeGeofences =
                geofenceRepository.findByActiveTrue();

        for (Geofence geofence : activeGeofences) {

            geofenceEventService.processLocation(
                    vehicle,
                    geofence,
                    latitude,
                    longitude
            );
        }
    }

    public Page<LocationResponse> getLocationHistory(
            Long vehicleId,
            Pageable pageable
    ) {

        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Vehicle not found: " + vehicleId
                        )
                );

        return locationRepository
                .findByVehicleOrderByRecordedAtDesc(
                        vehicle,
                        pageable
                )
                .map(this::toResponse);
    }

    private void validateLocation(LocationRequest request) {

        if (request.getLatitude() == null ||
                request.getLatitude() < -90 ||
                request.getLatitude() > 90) {

            throw new IllegalArgumentException(
                    "Latitude must be between -90 and 90"
            );
        }

        if (request.getLongitude() == null ||
                request.getLongitude() < -180 ||
                request.getLongitude() > 180) {

            throw new IllegalArgumentException(
                    "Longitude must be between -180 and 180"
            );
        }

        if (request.getSpeed() != null &&
                request.getSpeed() < 0) {

            throw new IllegalArgumentException(
                    "Speed cannot be negative"
            );
        }
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