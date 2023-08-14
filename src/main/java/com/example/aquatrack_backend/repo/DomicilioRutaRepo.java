package com.example.aquatrack_backend.repo;

import com.example.aquatrack_backend.model.DomicilioRuta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DomicilioRutaRepo extends JpaRepository<DomicilioRuta, Long> {
}
