package com.fazil.production_backend.repository;

import com.fazil.production_backend.entity.Alert;
import com.fazil.production_backend.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlertRepository extends JpaRepository<Alert, Long> {

    List<Alert> findByOrderByCreatedAtDesc();

    List<Alert> findByVehicleOrderByCreatedAtDesc(
            Vehicle vehicle
    );
}