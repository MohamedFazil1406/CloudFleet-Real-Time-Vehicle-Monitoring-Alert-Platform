package com.fazil.production_backend.dto;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class DashboardResponse {

    private long totalVehicles;
    private long activeVehicles;
    private long inactiveVehicles;
    private long totalAlerts;

    private List<VehicleResponse> vehicles;
    private List<AlertResponse> recentAlerts;

}