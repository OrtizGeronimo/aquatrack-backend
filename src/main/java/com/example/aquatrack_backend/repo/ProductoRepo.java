package com.example.aquatrack_backend.repo;

import com.example.aquatrack_backend.model.Producto;
import com.example.aquatrack_backend.model.Rol;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductoRepo extends RepoBase<Producto> {

    @Query(value = "SELECT * FROM producto p " +
                 "INNER JOIN precio pr ON p.id = pr.producto_id " + 
                 "WHERE :id = p.empresa_id " +
                 "AND pr.fecha_fin_vigencia is NULL " + 
                 "AND (p.fecha_fin_vigencia is NULL or :mostrarInactivos = true) " +
                 "AND (:nombre IS NULL OR p.nombre LIKE %:nombre%) " +
                 "ORDER BY p.id", 
                 nativeQuery = true)
    Page<Producto> getProductosActivos(Long id, String nombre, boolean mostrarInactivos, Pageable pageable);
}

