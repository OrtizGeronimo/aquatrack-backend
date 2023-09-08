package com.example.aquatrack_backend.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.aquatrack_backend.model.Producto;

@Repository
public interface ProductoRepo extends RepoBase<Producto> {

    @Query(value = "SELECT * " + 
                 "FROM producto p " +
                 "INNER JOIN precio pr ON p.id = pr.producto_id " + 
                 "WHERE :id = p.empresa_id " +
                 "AND pr.fecha_fin_vigencia is NULL " + 
                 "AND (p.fecha_fin_vigencia is NULL or :mostrarInactivos = true) " +
                 "AND (:nombre IS NULL OR p.nombre LIKE %:nombre%)",
                //  "ORDER BY p.id", 
                 nativeQuery = true)
    Page<Producto> getProductosActivos(Long id, String nombre, boolean mostrarInactivos, Pageable pageable);
}

