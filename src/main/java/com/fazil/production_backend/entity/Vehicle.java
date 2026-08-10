package com.fazil.production_backend.entity;

import com.fazil.production_backend.enums.VehicleStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "vehicles")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String vehicleNumber;

    @Column(nullable = false)
    private String type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VehicleStatus status;

    private Double latitude;

    private Double longitude;

    private Double speed;

    private LocalDateTime lastUpdated;
}