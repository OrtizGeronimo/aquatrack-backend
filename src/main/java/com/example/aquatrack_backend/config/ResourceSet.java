package com.example.aquatrack_backend.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
class ResourceSet {

    @JsonProperty("resources")
    private List<Resource> resources;

    public List<Resource> getResources() {
        return resources;
    }
}
