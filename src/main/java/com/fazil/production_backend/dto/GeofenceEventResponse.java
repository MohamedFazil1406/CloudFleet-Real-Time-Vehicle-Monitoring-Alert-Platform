package com.fazil.production_backend.dto;

import com.fazil.production_backend.enums.GeofenceEventType;
import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class GeofenceEventResponse {

    private Long id;
    private Long vehicleId;
    private Long geofenceId;
    private String geofenceName;
    private GeofenceEventType eventType;
    private Double latitude;
    private Double longitude;
    private LocalDateTime occurredAt;

}