package com.example.aquatrack_backend.repo;

import com.example.aquatrack_backend.model.Reparto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RepartoRepo extends RepoBase<Reparto> {

    @Query(value = "SELECT * FROM reparto r JOIN estado_reparto er ON r.estado_reparto_id = er.id JOIN ruta ru ON ru.id = r.ruta_id " +
            " WHERE " +
            "(:estado IS NULL OR er.id = :estado) " +
            "AND (:nombreRuta IS NULL OR ru.nombre LIKE %:nombreRuta%) " +
            "AND (:cantidadEntregaDesde IS NULL OR (SELECT COUNT(id) FROM entrega WHERE reparto_id = r.id) >= :cantidadEntregaDesde) " +
            "AND (:cantidadEntregaHasta IS NULL OR (SELECT COUNT(id) FROM entrega WHERE reparto_id = r.id) < :cantidadEntregaHasta) ORDER BY er.id, ru.nombre "
            ,nativeQuery = true)
    Page<Reparto> search(String nombreRuta, Integer cantidadEntregaDesde, Integer cantidadEntregaHasta, Integer estado, Pageable pageable);
}
