package com.fazil.production_backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "vehicle_geofence_states",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {"vehicle_id", "geofence_id"}
                )
        }
)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class VehicleGeofenceState {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "geofence_id", nullable = false)
    private Geofence geofence;

    @Column(nullable = false)
    private Boolean inside;

    private LocalDateTime lastUpdated;

}