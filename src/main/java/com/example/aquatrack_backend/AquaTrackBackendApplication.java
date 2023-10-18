package com.example.aquatrack_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AquaTrackBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(AquaTrackBackendApplication.class, args);
    }
}
