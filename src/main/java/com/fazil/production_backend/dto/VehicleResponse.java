package com.fazil.production_backend.dto;

import com.fazil.production_backend.enums.VehicleStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VehicleResponse {

    private Long id;
    private String vehicleNumber;
    private String type;
    private VehicleStatus status;
    private Double latitude;
    private Double longitude;
    private Double speed;
    private LocalDateTime lastUpdated;


}