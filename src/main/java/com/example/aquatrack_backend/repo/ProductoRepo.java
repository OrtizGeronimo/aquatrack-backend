package com.example.aquatrack_backend.repo;

import com.example.aquatrack_backend.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductoRepo extends JpaRepository<Producto, Long> {
}