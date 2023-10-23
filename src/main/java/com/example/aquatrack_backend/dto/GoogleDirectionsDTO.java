package com.example.aquatrack_backend.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GoogleDirectionsDTO {
    private Double latitude;
    private Double longitude;
}
