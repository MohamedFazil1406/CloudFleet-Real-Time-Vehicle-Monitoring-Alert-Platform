package com.fazil.production_backend.controller;

import com.fazil.production_backend.dto.LocationResponse;
import com.fazil.production_backend.service.LocationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/vehicles")
public class VehicleHistoryController {

    private final LocationService locationService;

    public VehicleHistoryController(
            LocationService locationService
    ) {
        this.locationService = locationService;
    }

    @GetMapping("/{vehicleId}/history")
    public ResponseEntity<Page<LocationResponse>> getVehicleHistory(
            @PathVariable Long vehicleId,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "20")
            int size
    ) {

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(
                        Sort.Direction.DESC,
                        "recordedAt"
                )
        );

        return ResponseEntity.ok(
                locationService.getLocationHistory(
                        vehicleId,
                        pageable
                )
        );
    }
}