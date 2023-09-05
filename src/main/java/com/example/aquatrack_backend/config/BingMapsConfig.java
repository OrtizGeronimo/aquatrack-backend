package com.example.aquatrack_backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BingMapsConfig {

//    @Value("{bing.maps.key}")
//    private String apiKey;

    public String getApiKey() {
        return "AvPjAjZr0WHKqHppFv1ypP1BcVFBoNKCCweh8S322AkbQ7uf-7CG_nTRYMFqbZVm";
    }
}
