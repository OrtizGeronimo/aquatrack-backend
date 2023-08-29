package com.example.aquatrack_backend.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class DistanceMatrixResponse {

    @JsonProperty("resourceSets")
    private List<ResourceSet> resourceSets;

}

