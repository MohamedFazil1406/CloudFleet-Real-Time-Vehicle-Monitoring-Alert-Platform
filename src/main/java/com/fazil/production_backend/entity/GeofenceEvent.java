package com.fazil.production_backend.entity;

import com.fazil.production_backend.enums.GeofenceEventType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "geofence_events")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class GeofenceEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "geofence_id", nullable = false)
    private Geofence geofence;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GeofenceEventType eventType;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Column(nullable = false)
    private LocalDateTime occurredAt;

}