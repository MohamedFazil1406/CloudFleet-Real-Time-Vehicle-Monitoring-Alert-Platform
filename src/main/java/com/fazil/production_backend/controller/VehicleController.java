package com.fazil.production_backend.controller;

import com.fazil.production_backend.dto.VehicleRequest;
import com.fazil.production_backend.dto.VehicleResponse;
import com.fazil.production_backend.service.VehicleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vehicles")
public class VehicleController {

    private final VehicleService vehicleService;

    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    // Create vehicle
    @PostMapping
    public ResponseEntity<VehicleResponse> createVehicle(
            @RequestBody VehicleRequest request
    ) {
        VehicleResponse response = vehicleService.createVehicle(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // Get all vehicles
    @GetMapping
    public ResponseEntity<List<VehicleResponse>> getAllVehicles() {

        return ResponseEntity.ok(
                vehicleService.getAllVehicles()
        );
    }

    // Get vehicle by ID
    @GetMapping("/{id}")
    public ResponseEntity<VehicleResponse> getVehicle(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                vehicleService.getVehicle(id)
        );
    }

    // Update vehicle
    @PutMapping("/{id}")
    public ResponseEntity<VehicleResponse> updateVehicle(
            @PathVariable Long id,
            @RequestBody VehicleRequest request
    ) {

        return ResponseEntity.ok(
                vehicleService.updateVehicle(id, request)
        );
    }

    // Delete vehicle
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVehicle(
            @PathVariable Long id
    ) {

        vehicleService.deleteVehicle(id);

        return ResponseEntity.noContent().build();
    }
}