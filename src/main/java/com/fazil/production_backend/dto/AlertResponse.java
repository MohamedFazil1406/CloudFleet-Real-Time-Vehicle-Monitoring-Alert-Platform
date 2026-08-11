package com.fazil.production_backend.dto;

import com.fazil.production_backend.enums.AlertType;
import lombok.*;

import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class AlertResponse {

    private Long id;
    private Long vehicleId;
    private Long geofenceId;
    private String geofenceName;
    private AlertType type;
    private String message;
    private Double latitude;
    private Double longitude;
    private LocalDateTime createdAt;

}