package com.example.aquatrack_backend.repo;

import com.example.aquatrack_backend.dto.ProductoEntregadoProjection;
import com.example.aquatrack_backend.model.Producto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoRepo extends RepoBase<Producto> {

    @Query(value = "SELECT * FROM producto " + "INNER JOIN precio pr ON producto.id = pr.producto_id " + "WHERE :id = producto.empresa_id " + "AND pr.fecha_fin_vigencia is NULL " + "AND (producto.fecha_fin_vigencia is NULL or :mostrarInactivos = true) " + "AND (:mostrar_retornables = false OR producto.retornable = 1) " + "AND (:nombre IS NULL OR producto.nombre LIKE %:nombre%) " + "AND :precio1 <= pr.precio AND :precio2 >= pr.precio " + "ORDER BY producto.id", countQuery = "SELECT COUNT(id) FROM producto", nativeQuery = true)
    Page<Producto> getProductosActivos(Long id, String nombre, boolean mostrarInactivos, boolean mostrar_retornables, int precio1, int precio2, Pageable pageable);

    @Query(value = "SELECT * FROM producto " + "WHERE codigo LIKE %:code% " + "AND empresa_id = :id", nativeQuery = true)
    Optional<Producto> findByCode(String code, Long id);

    @Query(value = "SELECT * FROM producto p " + "WHERE p.fecha_fin_vigencia IS NULL AND p.empresa_id = :empresa_id", nativeQuery = true)
    List<Producto> findProductsByEmpresa(@Param("empresa_id") Long empresaId);

    Producto findProductoById(Long id);

    @Query(value = "SELECT * FROM producto " +
            "WHERE empresa_id = :idEmpresa " +
            "AND fecha_fin_vigencia is NULL", nativeQuery = true)
    List<Producto> getAllProductos(@Param("idEmpresa") Long idEmpresa);

    @Query(value = "SELECT count(*) as productos FROM producto p " +
            "WHERE p.codigo = :codigo AND p.empresa_id = :empresaId", nativeQuery = true)
    Integer validateCodigoUnico(@Param("codigo") String codigo, @Param("empresaId") Long empresaId);

    @Query(value = "SELECT p.nombre as nombre, sum(ed.cantidad_entregada) as cantidad FROM producto p INNER JOIN entrega_detalle ed ON ed.producto_id = p.id INNER JOIN entrega e ON e.id = ed.entrega_id WHERE p.empresa_id = :id_empresa AND e.fecha_creacion >= :fecha_desde AND e.fecha_creacion <= :fecha_hasta GROUP BY (p.id)", nativeQuery = true)
    List<ProductoEntregadoProjection> getProductosEntregados(@Param("fecha_desde") LocalDate fechaDesde, @Param("fecha_hasta") LocalDate fechaHasta, @Param("id_empresa") Long idEmpresa);
}

