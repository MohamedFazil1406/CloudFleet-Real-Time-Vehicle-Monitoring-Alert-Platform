package com.fazil.production_backend.repository;

import com.fazil.production_backend.entity.GeofenceEvent;
import com.fazil.production_backend.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GeofenceEventRepository
        extends JpaRepository<GeofenceEvent, Long> {

    List<GeofenceEvent> findByVehicleOrderByOccurredAtDesc(
            Vehicle vehicle
    );
}