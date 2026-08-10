package com.fazil.production_backend.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class LocationRequest {

    private Double latitude;
    private Double longitude;
    private Double speed;


}