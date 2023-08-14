package com.example.aquatrack_backend.repo;

import com.example.aquatrack_backend.model.DomicilioProducto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DomicilioProductoRepo extends JpaRepository<DomicilioProducto, Long> {
}
