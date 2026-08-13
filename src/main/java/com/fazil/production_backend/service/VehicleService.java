package com.fazil.production_backend.service;

import com.fazil.production_backend.dto.VehicleRequest;
import com.fazil.production_backend.dto.VehicleResponse;
import com.fazil.production_backend.entity.Geofence;
import com.fazil.production_backend.entity.Vehicle;
import com.fazil.production_backend.enums.VehicleStatus;
import com.fazil.production_backend.repository.GeofenceRepository;
import com.fazil.production_backend.repository.VehicleRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class VehicleService {

    private final VehicleRepository vehicleRepository;

    private final GeofenceRepository geofenceRepository;

    private final GeofenceEventService geofenceEventService;

    public VehicleService(
            VehicleRepository vehicleRepository,
            GeofenceRepository geofenceRepository,
            GeofenceEventService geofenceEventService
    ) {
        this.vehicleRepository = vehicleRepository;
        this.geofenceRepository = geofenceRepository;
        this.geofenceEventService = geofenceEventService;
    }

    /* ========================================================= */
    /* RESPONSE MAPPER                                            */
    /* ========================================================= */

    private VehicleResponse toResponse(
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

    /* ========================================================= */
    /* FIND VEHICLE                                               */
    /* ========================================================= */

    private Vehicle findVehicle(
            Long id
    ) {

        return vehicleRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Vehicle not found: " + id
                        )
                );
    }

    /* ========================================================= */
    /* CREATE VEHICLE                                             */
    /* ========================================================= */

    public VehicleResponse createVehicle(
            VehicleRequest request
    ) {

        if (
                request.getVehicleNumber() == null ||
                        request.getVehicleNumber().isBlank()
        ) {

            throw new IllegalArgumentException(
                    "Vehicle number is required"
            );
        }

        if (
                vehicleRepository.existsByVehicleNumber(
                        request.getVehicleNumber()
                )
        ) {

            throw new IllegalArgumentException(
                    "Vehicle already exists: "
                            + request.getVehicleNumber()
            );
        }

        validateLocation(
                request.getLatitude(),
                request.getLongitude()
        );

        validateSpeed(
                request.getSpeed()
        );

        Vehicle vehicle =
                new Vehicle();

        vehicle.setVehicleNumber(
                request.getVehicleNumber()
        );

        vehicle.setType(
                request.getType()
        );

        vehicle.setStatus(
                request.getStatus() != null
                        ? request.getStatus()
                        : VehicleStatus.INACTIVE
        );

        vehicle.setLatitude(
                request.getLatitude()
        );

        vehicle.setLongitude(
                request.getLongitude()
        );

        vehicle.setSpeed(
                request.getSpeed()
        );

        vehicle.setLastUpdated(
                LocalDateTime.now()
        );

        Vehicle savedVehicle =
                vehicleRepository.save(vehicle);

        return toResponse(
                savedVehicle
        );
    }

    /* ========================================================= */
    /* GET ALL VEHICLES                                           */
    /* ========================================================= */

    public List<VehicleResponse> getAllVehicles() {

        return vehicleRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    /* ========================================================= */
    /* GET VEHICLE                                                */
    /* ========================================================= */

    public VehicleResponse getVehicle(
            Long id
    ) {

        Vehicle vehicle =
                findVehicle(id);

        return toResponse(vehicle);
    }

    /* ========================================================= */
    /* UPDATE VEHICLE                                             */
    /* ========================================================= */

    public VehicleResponse updateVehicle(
            Long id,
            VehicleRequest request
    ) {

        Vehicle vehicle =
                findVehicle(id);

        if (
                request.getVehicleNumber() == null ||
                        request.getVehicleNumber().isBlank()
        ) {

            throw new IllegalArgumentException(
                    "Vehicle number is required"
            );
        }

        validateLocation(
                request.getLatitude(),
                request.getLongitude()
        );

        validateSpeed(
                request.getSpeed()
        );

        /*
         * Prevent another vehicle from using
         * the same vehicle number.
         */
        if (
                !vehicle.getVehicleNumber()
                        .equals(request.getVehicleNumber())
        ) {

            boolean exists =
                    vehicleRepository
                            .existsByVehicleNumber(
                                    request.getVehicleNumber()
                            );

            if (exists) {

                throw new IllegalArgumentException(
                        "Vehicle already exists: "
                                + request.getVehicleNumber()
                );
            }
        }

        /*
         * Update vehicle data.
         */
        vehicle.setVehicleNumber(
                request.getVehicleNumber()
        );

        vehicle.setType(
                request.getType()
        );

        if (
                request.getStatus() != null
        ) {

            vehicle.setStatus(
                    request.getStatus()
            );
        }

        vehicle.setLatitude(
                request.getLatitude()
        );

        vehicle.setLongitude(
                request.getLongitude()
        );

        vehicle.setSpeed(
                request.getSpeed()
        );

        vehicle.setLastUpdated(
                LocalDateTime.now()
        );

        /*
         * Save latest vehicle location first.
         */
        Vehicle updatedVehicle =
                vehicleRepository.save(vehicle);

        /*
         * =====================================================
         * GEOFENCE DETECTION
         * =====================================================
         *
         * Every time the vehicle location changes,
         * check it against every active geofence.
         */
        processGeofences(
                updatedVehicle
        );

        return toResponse(
                updatedVehicle
        );
    }

    /* ========================================================= */
    /* PROCESS ACTIVE GEOFENCES                                   */
    /* ========================================================= */

    private void processGeofences(
            Vehicle vehicle
    ) {

        /*
         * If vehicle has no coordinates,
         * geofence detection cannot happen.
         */
        if (
                vehicle.getLatitude() == null ||
                        vehicle.getLongitude() == null
        ) {

            return;
        }

        /*
         * Get all active geofences.
         */
        List<Geofence> activeGeofences =
                geofenceRepository
                        .findByActiveTrue();

        /*
         * Check vehicle against every
         * active geofence.
         */
        for (
                Geofence geofence :
                activeGeofences
        ) {

            try {

                geofenceEventService.processLocation(
                        vehicle,
                        geofence,
                        vehicle.getLatitude(),
                        vehicle.getLongitude()
                );

            } catch (Exception exception) {

                /*
                 * Don't allow one broken geofence
                 * to prevent the vehicle update.
                 */
                System.err.println(
                        "Failed to process geofence "
                                + geofence.getName()
                                + " for vehicle "
                                + vehicle.getVehicleNumber()
                                + ": "
                                + exception.getMessage()
                );
            }
        }
    }

    /* ========================================================= */
    /* DELETE VEHICLE                                             */
    /* ========================================================= */

    public void deleteVehicle(
            Long id
    ) {

        Vehicle vehicle =
                findVehicle(id);

        vehicleRepository.delete(
                vehicle
        );
    }

    /* ========================================================= */
    /* VALIDATION                                                 */
    /* ========================================================= */

    private void validateLocation(
            Double latitude,
            Double longitude
    ) {

        if (
                latitude == null
        ) {

            throw new IllegalArgumentException(
                    "Latitude is required"
            );
        }

        if (
                longitude == null
        ) {

            throw new IllegalArgumentException(
                    "Longitude is required"
            );
        }

        if (
                latitude < -90 ||
                        latitude > 90
        ) {

            throw new IllegalArgumentException(
                    "Latitude must be between -90 and 90"
            );
        }

        if (
                longitude < -180 ||
                        longitude > 180
        ) {

            throw new IllegalArgumentException(
                    "Longitude must be between -180 and 180"
            );
        }
    }

    private void validateSpeed(
            Double speed
    ) {

        if (
                speed != null &&
                        speed < 0
        ) {

            throw new IllegalArgumentException(
                    "Speed cannot be negative"
            );
        }
    }
}