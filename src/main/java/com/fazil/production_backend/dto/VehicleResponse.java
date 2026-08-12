package com.fazil.production_backend.dto;

import com.fazil.production_backend.enums.VehicleStatus;
import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
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