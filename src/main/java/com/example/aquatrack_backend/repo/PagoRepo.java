package com.example.aquatrack_backend.repo;

import com.example.aquatrack_backend.model.Pago;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface PagoRepo extends RepoBase<Pago> {
    @Query(value = "SELECT DISTINCT p.* FROM pago p " +
            "JOIN deuda_pago dp ON p.id = dp.pago_id " +
            "JOIN deuda d ON dp.deuda_id = d.id " +
            "JOIN domicilio dom ON dom.deuda_id = d.id " +
            "LEFT JOIN entrega e ON dom.id = e.domicilio_id " +
            "WHERE (:idMedioPago IS NULL OR medio_pago_id = :idMedioPago) " +
            "AND dom.id = :idDomicilio " +
            "AND (:idEmpleado IS NULL OR empleado_id = :idEmpleado) " +
            "AND (:fechaCreacionDesde IS NULL OR fecha_pago >= :fechaCreacionDesde) " +
            "AND (:fechaCreacionHasta IS NULL OR fecha_pago <= :fechaCreacionHasta) " +
            "AND (:montoDesde IS NULL OR total >= :montoDesde) " +
            "AND (:montoHasta IS NULL OR total <= :montoHasta) " +
            "ORDER BY p.fecha_pago DESC",
            nativeQuery = true)
    List<Pago> findAllPagosFromClient(@Param("idDomicilio") Long idDomicilio,
                                      @Param("idMedioPago") Long idMedioPago,
                                      @Param("idEmpleado") Long idEmpleado,
                                      @Param("fechaCreacionDesde") LocalDate fechaCreacionDesde,
                                      @Param("fechaCreacionHasta") LocalDate fechaCreacionHasta,
                                      @Param("montoDesde") BigDecimal montoDesde,
                                      @Param("montoHasta") BigDecimal montoHasta);

    @Query(value = "SELECT sum(p.total) FROM pago p INNER JOIN empleado e ON e.id = p.empleado_id WHERE e.empresa_id = :id_empresa AND p.fecha_pago >= :fecha_desde AND p.fecha_pago <= :fecha_hasta", nativeQuery = true)
    BigDecimal getRecaudado(@Param("fecha_desde") LocalDate fechaDesde, @Param("fecha_hasta") LocalDate fechaHasta, @Param("id_empresa") Long idEmpresa);
}
