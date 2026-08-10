package com.fazil.production_backend.service;

import com.fazil.production_backend.dto.VehicleRequest;
import com.fazil.production_backend.dto.VehicleResponse;
import com.fazil.production_backend.entity.Vehicle;
import com.fazil.production_backend.enums.VehicleStatus;
import com.fazil.production_backend.repository.VehicleRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class VehicleService {

    private final VehicleRepository vehicleRepository;

    public VehicleService(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    private VehicleResponse toResponse(Vehicle vehicle) {

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

    private Vehicle findVehicle(Long id) {

        return vehicleRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Vehicle not found: " + id)
                );
    }

    public VehicleResponse createVehicle(VehicleRequest request) {

        if (vehicleRepository.existsByVehicleNumber(
                request.getVehicleNumber()
        )) {
            throw new RuntimeException("Vehicle already exists");
        }

        Vehicle vehicle = new Vehicle();

        vehicle.setVehicleNumber(request.getVehicleNumber());
        vehicle.setType(request.getType());

        vehicle.setStatus(
                request.getStatus() != null
                        ? request.getStatus()
                        : VehicleStatus.INACTIVE
        );

        vehicle.setLatitude(request.getLatitude());
        vehicle.setLongitude(request.getLongitude());
        vehicle.setSpeed(request.getSpeed());
        vehicle.setLastUpdated(LocalDateTime.now());

        Vehicle savedVehicle = vehicleRepository.save(vehicle);

        return toResponse(savedVehicle);
    }

    public List<VehicleResponse> getAllVehicles() {

        return vehicleRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public VehicleResponse getVehicle(Long id) {

        Vehicle vehicle = findVehicle(id);

        return toResponse(vehicle);
    }

    public VehicleResponse updateVehicle(
            Long id,
            VehicleRequest request
    ) {

        Vehicle vehicle = findVehicle(id);

        vehicle.setVehicleNumber(request.getVehicleNumber());
        vehicle.setType(request.getType());

        if (request.getStatus() != null) {
            vehicle.setStatus(request.getStatus());
        }

        vehicle.setLatitude(request.getLatitude());
        vehicle.setLongitude(request.getLongitude());
        vehicle.setSpeed(request.getSpeed());
        vehicle.setLastUpdated(LocalDateTime.now());

        Vehicle updatedVehicle = vehicleRepository.save(vehicle);

        return toResponse(updatedVehicle);
    }

    public void deleteVehicle(Long id) {

        Vehicle vehicle = findVehicle(id);

        vehicleRepository.delete(vehicle);
    }
}