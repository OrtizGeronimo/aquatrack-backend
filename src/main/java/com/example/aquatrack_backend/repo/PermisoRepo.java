package com.example.aquatrack_backend.repo;

import com.example.aquatrack_backend.model.Permiso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PermisoRepo extends JpaRepository<Permiso, Long> {
}