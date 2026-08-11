package com.fazil.production_backend.repository;

import com.fazil.production_backend.entity.Geofence;
import com.fazil.production_backend.entity.Vehicle;
import com.fazil.production_backend.entity.VehicleGeofenceState;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VehicleGeofenceStateRepository
        extends JpaRepository<VehicleGeofenceState, Long> {

    Optional<VehicleGeofenceState> findByVehicleAndGeofence(
            Vehicle vehicle,
            Geofence geofence
    );
}