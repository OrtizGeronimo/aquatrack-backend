package com.example.aquatrack_backend.repo;

import com.example.aquatrack_backend.model.Domicilio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DomicilioRepo extends JpaRepository<Domicilio, Long> {
}
