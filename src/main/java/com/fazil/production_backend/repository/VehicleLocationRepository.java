package com.fazil.production_backend.repository;

import com.fazil.production_backend.entity.Vehicle;
import com.fazil.production_backend.entity.VehicleLocation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VehicleLocationRepository
        extends JpaRepository<VehicleLocation, Long> {

    List<VehicleLocation> findByVehicleOrderByRecordedAtDesc(
            Vehicle vehicle
    );
}