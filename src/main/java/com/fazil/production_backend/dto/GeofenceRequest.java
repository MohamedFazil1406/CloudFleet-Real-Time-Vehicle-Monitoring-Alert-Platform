package com.fazil.production_backend.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class GeofenceRequest {

    private String name;

    private Double centerLatitude;

    private Double centerLongitude;

    private Double radiusMeters;

    private Boolean active;

}