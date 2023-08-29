package com.example.aquatrack_backend.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
class Result {
    @JsonProperty("travelDistance")
    private double travelDistance;

    @JsonProperty("travelDuration")
    private double travelDuration;

}
