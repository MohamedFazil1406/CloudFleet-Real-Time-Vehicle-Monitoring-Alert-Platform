package com.fazil.production_backend.dto;

import com.fazil.production_backend.enums.VehicleStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VehicleRequest {

    private String vehicleNumber;
    private String type;
    private VehicleStatus status;
    private Double latitude;
    private Double longitude;
    private Double speed;

}