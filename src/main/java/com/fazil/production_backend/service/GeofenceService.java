package com.fazil.production_backend.service;

import com.fazil.production_backend.dto.GeofenceRequest;
import com.fazil.production_backend.dto.GeofenceResponse;
import com.fazil.production_backend.entity.Geofence;
import com.fazil.production_backend.repository.GeofenceRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class GeofenceService {

    private final GeofenceRepository geofenceRepository;

    public GeofenceService(GeofenceRepository geofenceRepository) {
        this.geofenceRepository = geofenceRepository;
    }

    private GeofenceResponse toResponse(Geofence geofence) {

        return new GeofenceResponse(
                geofence.getId(),
                geofence.getName(),
                geofence.getCenterLatitude(),
                geofence.getCenterLongitude(),
                geofence.getRadiusMeters(),
                geofence.getActive(),
                geofence.getCreatedAt()
        );
    }

    private Geofence findGeofence(Long id) {

        return geofenceRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Geofence not found: " + id
                        )
                );
    }

    private void validateCoordinates(
            Double latitude,
            Double longitude
    ) {

        if (latitude == null || latitude < -90 || latitude > 90) {
            throw new IllegalArgumentException(
                    "Latitude must be between -90 and 90"
            );
        }

        if (longitude == null || longitude < -180 || longitude > 180) {
            throw new IllegalArgumentException(
                    "Longitude must be between -180 and 180"
            );
        }
    }

    private void validateRadius(Double radiusMeters) {

        if (radiusMeters == null || radiusMeters <= 0) {
            throw new IllegalArgumentException(
                    "Radius must be greater than 0"
            );
        }
    }

    public GeofenceResponse createGeofence(
            GeofenceRequest request
    ) {

        if (request.getName() == null ||
                request.getName().isBlank()) {

            throw new IllegalArgumentException(
                    "Geofence name is required"
            );
        }

        if (geofenceRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException(
                    "Geofence already exists: " + request.getName()
            );
        }

        validateCoordinates(
                request.getCenterLatitude(),
                request.getCenterLongitude()
        );

        validateRadius(request.getRadiusMeters());

        Geofence geofence = new Geofence();

        geofence.setName(request.getName());
        geofence.setCenterLatitude(
                request.getCenterLatitude()
        );
        geofence.setCenterLongitude(
                request.getCenterLongitude()
        );
        geofence.setRadiusMeters(
                request.getRadiusMeters()
        );

        geofence.setActive(
                request.getActive() != null
                        ? request.getActive()
                        : true
        );

        geofence.setCreatedAt(LocalDateTime.now());

        Geofence savedGeofence =
                geofenceRepository.save(geofence);

        return toResponse(savedGeofence);
    }

    public List<GeofenceResponse> getAllGeofences() {

        return geofenceRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<GeofenceResponse> getActiveGeofences() {

        return geofenceRepository.findByActiveTrue()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public GeofenceResponse getGeofence(Long id) {

        return toResponse(findGeofence(id));
    }

    public GeofenceResponse updateGeofence(
            Long id,
            GeofenceRequest request
    ) {

        Geofence geofence = findGeofence(id);

        if (request.getName() == null ||
                request.getName().isBlank()) {

            throw new IllegalArgumentException(
                    "Geofence name is required"
            );
        }

        validateCoordinates(
                request.getCenterLatitude(),
                request.getCenterLongitude()
        );

        validateRadius(request.getRadiusMeters());

        if (!geofence.getName().equals(request.getName())
                && geofenceRepository.existsByName(
                request.getName()
        )) {

            throw new IllegalArgumentException(
                    "Geofence already exists: " + request.getName()
            );
        }

        geofence.setName(request.getName());

        geofence.setCenterLatitude(
                request.getCenterLatitude()
        );

        geofence.setCenterLongitude(
                request.getCenterLongitude()
        );

        geofence.setRadiusMeters(
                request.getRadiusMeters()
        );

        if (request.getActive() != null) {
            geofence.setActive(request.getActive());
        }

        Geofence updatedGeofence =
                geofenceRepository.save(geofence);

        return toResponse(updatedGeofence);
    }

    public void deleteGeofence(Long id) {

        Geofence geofence = findGeofence(id);

        geofenceRepository.delete(geofence);
    }
}