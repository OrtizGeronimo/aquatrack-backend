package com.example.aquatrack_backend.repo;

import com.example.aquatrack_backend.model.Reparto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RepartoRepo extends RepoBase<Reparto> {

    @Query(value = "SELECT * FROM reparto r JOIN estado_reparto er ON r.estado_reparto_id = er.id JOIN ruta ru ON ru.id = r.ruta_id LEFT JOIN empleado e ON e.id = r.repartidor_id " +
            "WHERE " +
            "(:idEstado IS NULL OR er.id = :idEstado) " +
            "AND (:idRuta IS NULL OR ru.id = :idRuta) " +
            "AND (:fechaEjecucionDesde IS NULL OR fecha_ejecucion >= :fechaEjecucionDesde) " +
            "AND (:fechaEjecucionHasta IS NULL OR fecha_ejecucion <= :fechaEjecucionHasta) " +
            "AND (:idRepartidor IS NULL OR e.id = :idRepartidor) " +
            "AND ru.empresa_id = :empresaId " +
            "ORDER BY r.fecha_ejecucion DESC, er.id, ru.nombre"
            , countQuery = "SELECT COUNT(id) FROM reparto", nativeQuery = true)
    Page<Reparto> search(Long empresaId, Long idRuta, Long idRepartidor, Long idEstado, LocalDate fechaEjecucionDesde, LocalDate fechaEjecucionHasta, Pageable pageable);

    @Query(value = "SELECT * FROM reparto r WHERE repartidor_id = :id_repartidor AND DATE(fecha_ejecucion) = CURRENT_DATE AND estado_reparto_id = :id_estado", nativeQuery = true)
    List<Reparto> findRepartosAsignadosHoy(@Param("id_repartidor") Long idRepartidor, @Param("id_estado") Long idEstado);

    List<Reparto> findRepartosByRepartidorIdAndEstadoRepartoId(Long idRepartidor, Long idEstadoReparto);

    @Query(value = "SELECT * FROM reparto r JOIN estado_reparto er ON r.estado_reparto_id = er.id JOIN ruta ru ON ru.id = r.ruta_id " +
            " WHERE " +
            "(:idEstado IS NULL OR er.id = :idEstado) " +
            "AND (:idRuta IS NULL OR ru.id = :idRuta) " +
            "AND (:fechaEjecucionDesde IS NULL OR fecha_ejecucion >= :fechaEjecucionDesde) " +
            "AND (:fechaEjecucionHasta IS NULL OR fecha_ejecucion <= :fechaEjecucionHasta) " +
            "AND r.repartidor_id = :idRepartidor " +
            "ORDER BY r.fecha_ejecucion DESC, er.id, ru.nombre"
            , nativeQuery = true)
    List<Reparto> searchMobile(Long idRuta, Long idRepartidor, Long idEstado, LocalDate fechaEjecucionDesde, LocalDate fechaEjecucionHasta);
}
