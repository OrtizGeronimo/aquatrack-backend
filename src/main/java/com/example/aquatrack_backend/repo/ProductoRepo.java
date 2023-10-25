package com.example.aquatrack_backend.repo;

import com.example.aquatrack_backend.model.Producto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoRepo extends RepoBase<Producto> {

    @Query(value = "SELECT * FROM producto " + "INNER JOIN precio pr ON producto.id = pr.producto_id " + "WHERE :id = producto.empresa_id " + "AND pr.fecha_fin_vigencia is NULL " + "AND (producto.fecha_fin_vigencia is NULL or :mostrarInactivos = true) " + "AND (:nombre IS NULL OR producto.nombre LIKE %:nombre%) " + "AND :precio1 <= pr.precio AND :precio2 >= pr.precio " + "ORDER BY producto.id", nativeQuery = true)
    Page<Producto> getProductosActivos(Long id, String nombre, boolean mostrarInactivos, int precio1, int precio2, Pageable pageable);

    @Query(value = "SELECT * FROM producto " + "WHERE codigo LIKE %:code% " + "AND empresa_id = :id", nativeQuery = true)
    Optional<Producto> findByCode(String code, Long id);

    @Query(value = "SELECT * FROM producto p " + "WHERE p.fecha_fin_vigencia IS NULL AND p.empresa_id = :empresa_id", nativeQuery = true)
    List<Producto> findProductsByEmpresa(@Param("empresa_id") Long empresaId);

    Producto findProductoById(Long id);
}

