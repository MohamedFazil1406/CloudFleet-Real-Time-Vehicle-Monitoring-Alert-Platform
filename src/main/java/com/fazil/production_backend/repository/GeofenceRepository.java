package com.fazil.production_backend.repository;

import com.fazil.production_backend.entity.Geofence;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GeofenceRepository extends JpaRepository<Geofence, Long> {

    Optional<Geofence> findByName(String name);

    boolean existsByName(String name);

    List<Geofence> findByActiveTrue();
}