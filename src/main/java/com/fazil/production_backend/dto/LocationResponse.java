package com.fazil.production_backend.dto;

import lombok.*;
import org.springframework.boot.convert.DataSizeUnit;

import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class LocationResponse {

    private Long id;
    private Long vehicleId;
    private Double latitude;
    private Double longitude;
    private Double speed;
    private LocalDateTime recordedAt;


}