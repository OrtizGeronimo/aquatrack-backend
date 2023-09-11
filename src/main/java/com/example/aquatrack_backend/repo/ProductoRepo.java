package com.example.aquatrack_backend.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.aquatrack_backend.model.Producto;

@Repository
public interface ProductoRepo extends RepoBase<Producto> {

    @Query(value = "SELECT * FROM producto " +
                 "INNER JOIN precio pr ON producto.id = pr.producto_id " + 
                 "WHERE :id = producto.empresa_id " +
                 "AND pr.fecha_fin_vigencia is NULL " + 
                 "AND (producto.fecha_fin_vigencia is NULL or :mostrarInactivos = true) " +
                 "AND (:nombre IS NULL OR producto.nombre LIKE %:nombre%) " + 
                 "ORDER BY producto.id", 
                 nativeQuery = true)
    Page<Producto> getProductosActivos(Long id, String nombre, boolean mostrarInactivos, Pageable pageable);
}

