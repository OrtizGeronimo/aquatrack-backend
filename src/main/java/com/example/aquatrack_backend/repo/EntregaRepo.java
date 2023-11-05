package com.example.aquatrack_backend.repo;

import com.example.aquatrack_backend.dto.EntregaEstadoProjection;
import com.example.aquatrack_backend.model.Entrega;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface EntregaRepo extends RepoBase<Entrega> {

    @Query(value = "SELECT * FROM entrega e " +
            "WHERE e.reparto_id = :idReparto", nativeQuery = true)
    List<Entrega> findAllByReparto(@Param("idReparto") Long idReparto);

    @Query(value = "SELECT e.* FROM entrega e JOIN reparto r ON r.id = e.reparto_id WHERE e.reparto_id = :id_reparto AND e.estado_entrega_id = 2 ORDER BY e.orden_visita ASC LIMIT 1", nativeQuery = true)
    List<Entrega> findProximaEntrega(@Param("id_reparto") Long idReparto);

    @Query(value = "SELECT SUM(precios.precio_total)\n" +
            "FROM(\n" +
            "\tSELECT pr.id, SUM(pp.cantidad * pre.precio) AS precio_total FROM entrega e \n" +
            "\tJOIN entrega_pedido ep ON e.id = ep.entrega_id \n" +
            "\tJOIN pedido p ON p.id = ep.pedido_id \n" +
            "\tJOIN pedido_producto pp ON p.id = pp.pedido_id\n" +
            "\tJOIN producto pr ON pp.producto_id = pr.id\n" +
            "\tJOIN precio pre ON pr.id = pre.producto_id\n" +
            "\tWHERE pre.fecha_fin_vigencia IS NULL AND e.id = :id_entrega\n" +
            "\tGROUP BY pr.id\n" +
            ") precios", nativeQuery = true)
    BigDecimal getMontoTotalByEntrega(@Param("id_entrega") Long idEntrega);

    @Query(value = "SELECT * FROM entrega e \n" +
            "JOIN reparto r ON e.reparto_id = r.id \n" +
            "WHERE date(r.fecha_ejecucion) = CURRENT_DATE \n" +
            "AND r.estado_reparto_id = 3\n" +
            "AND e.domicilio_id = :id\n" +
            "AND e.estado_entrega_id = 2 LIMIT 1", nativeQuery = true)
    Entrega entregaActualCliente(Long id);

    @Query(value = "SELECT * FROM entrega e INNER JOIN pago p ON e.pago_id = p.id " +
            "WHERE e.domicilio_id = :idDomicilio " +
            "AND (:fechaVisitaDesde IS NULL OR fecha_hora_visita >= :fechaVisitaDesde) " +
            "AND (:fechaVisitaHasta IS NULL OR fecha_hora_visita <= :fechaVisitaHasta) " +
            "AND (:sinPagar = false OR p.total = 0) " +
            "AND e.estado_entrega_id = 3 ORDER BY fecha_hora_visita DESC", nativeQuery = true)
    List<Entrega> getEntregasProcesadasCliente(Long idDomicilio, LocalDate fechaVisitaDesde, LocalDate fechaVisitaHasta, boolean sinPagar);

    @Query(value = "SELECT COUNT(*) FROM entrega e INNER JOIN reparto r ON e.reparto_id = r.id INNER JOIN ruta ru ON r.ruta_id = ru.id WHERE ru.empresa_id = :id_empresa AND e.estado_entrega_id = 3 AND e.fecha_creacion >= :fecha_desde AND e.fecha_creacion <= :fecha_hasta", nativeQuery = true)
    Long countEntregas(@Param("fecha_desde") LocalDate fechaDesde, @Param("fecha_hasta") LocalDate fechaHasta, @Param("id_empresa") Long empresa_id);

    @Query(value = "SELECT ee.nombre_estado_entrega as estado, COUNT(e.id) as cantidad FROM entrega e JOIN estado_entrega ee ON e.estado_entrega_id = ee.id JOIN reparto r ON r.id = e.reparto_id JOIN ruta ru ON ru.id = r.ruta_id WHERE ru.empresa_id = :id_empresa AND e.fecha_creacion >= :fecha_desde AND e.fecha_creacion <= :fecha_hasta AND e.estado_entrega_id IN (3, 4, 5) GROUP BY ee.id", nativeQuery = true)
    List<EntregaEstadoProjection> getEntregasByEstado(@Param("fecha_desde") LocalDate fechaDesde, @Param("fecha_hasta") LocalDate fechaHasta, @Param("id_empresa") Long empresa_id);
}