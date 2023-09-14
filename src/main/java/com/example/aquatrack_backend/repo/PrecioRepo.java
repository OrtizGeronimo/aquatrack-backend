package com.example.aquatrack_backend.repo;

import java.math.BigDecimal;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.aquatrack_backend.model.Precio;

@Repository
public interface PrecioRepo extends RepoBase<Precio> {

     @Query(value = "SELECT * FROM precio " +
                 "WHERE fecha_fin_vigencia IS NULL " +
                 "AND producto_id = :id", 
                 nativeQuery = true)
    Precio getPrecioActivo(Long id);
}
