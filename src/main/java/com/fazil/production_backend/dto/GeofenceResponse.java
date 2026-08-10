package com.fazil.production_backend.dto;

import lombok.*;

import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class GeofenceResponse {

    private Long id;
    private String name;
    private Double centerLatitude;
    private Double centerLongitude;
    private Double radiusMeters;
    private Boolean active;
    private LocalDateTime createdAt;

}