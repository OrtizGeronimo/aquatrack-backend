package com.example.aquatrack_backend.repo;

import com.example.aquatrack_backend.model.Producto;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductoRepo extends RepoBase<Producto> {

    @Query(value = "SELECT DISTINCT p.id, p.nombre, p.descripcion, p.fecha_fin_vigencia, p.empresa_id " +
                 "FROM producto p " +
                 "INNER JOIN empresa e ON e.id = p.empresa_id " +
                 "WHERE p.fecha_fin_vigencia is NULL AND e.id = :id", nativeQuery = true)
    List<Producto> getProductosActivos(Long id);

}
