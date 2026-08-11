package com.fazil.production_backend.repository;

import com.fazil.production_backend.entity.Vehicle;
import com.fazil.production_backend.entity.VehicleLocation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VehicleLocationRepository
        extends JpaRepository<VehicleLocation, Long> {

    Page<VehicleLocation> findByVehicleOrderByRecordedAtDesc(
            Vehicle vehicle,
            Pageable pageable
    );
}