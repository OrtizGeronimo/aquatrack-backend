package com.example.aquatrack_backend.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
class Resource {
    @JsonProperty("results")
    private List<Result> results;

}
