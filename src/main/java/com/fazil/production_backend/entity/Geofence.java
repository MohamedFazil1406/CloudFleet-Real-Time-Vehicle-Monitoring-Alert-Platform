package com.fazil.production_backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "geofences")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class Geofence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private Double centerLatitude;

    @Column(nullable = false)
    private Double centerLongitude;

    @Column(nullable = false)
    private Double radiusMeters;

    @Column(nullable = false)
    private Boolean active;

    private LocalDateTime createdAt;

}