package com.example.aquatrack_backend.repo;

import com.example.aquatrack_backend.model.Ubicacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UbicacionRepo extends JpaRepository<Ubicacion, Long> {
}
