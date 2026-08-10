package com.fazil.production_backend.controller;

import com.fazil.production_backend.dto.LocationRequest;
import com.fazil.production_backend.dto.LocationResponse;
import com.fazil.production_backend.service.LocationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vehicles")
public class LocationController {

    private final LocationService locationService;

    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    @PostMapping("/{vehicleId}/location")
    public ResponseEntity<LocationResponse> updateLocation(
            @PathVariable Long vehicleId,
            @RequestBody LocationRequest request
    ) {

        return ResponseEntity.ok(
                locationService.updateLocation(
                        vehicleId,
                        request
                )
        );
    }

    @GetMapping("/{vehicleId}/locations")
    public ResponseEntity<List<LocationResponse>> getLocationHistory(
            @PathVariable Long vehicleId
    ) {

        return ResponseEntity.ok(
                locationService.getLocationHistory(vehicleId)
        );
    }
}