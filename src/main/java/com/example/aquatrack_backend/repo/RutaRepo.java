package com.example.aquatrack_backend.repo;

import com.example.aquatrack_backend.dto.DomicilioProjection;
import com.example.aquatrack_backend.model.Ruta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RutaRepo extends RepoBase<Ruta> {

    @Query(value = "SELECT * FROM ruta " +
            "INNER JOIN (" +
            "SELECT DISTINCT ruta_id FROM dia_ruta WHERE (:idDiaSemana IS NULL or dia_semana_id = :idDiaSemana)" +
            ") dr ON ruta.id = dr.ruta_id " +
            "WHERE empresa_id = :empresaId" +
            " AND (:texto IS NULL or nombre LIKE %:texto%)" +
            " AND (:mostrar_inactivos = true OR fecha_fin_vigencia IS NULL)" +
            " GROUP BY ruta.id", countQuery = "SELECT COUNT(id) FROM ruta",
            nativeQuery = true)
    Page<Ruta> findAllByEmpresaPaged(@Param("empresaId") Long empresaId, @Param("texto") String texto, @Param("idDiaSemana") Long idDiaSemana, @Param("mostrar_inactivos") boolean mostrarInactivos, Pageable pageable);


    @Query(value = "SELECT DISTINCT d.id, d.calle, d.numero, d.piso_departamento, d.localidad, d.cliente_id as cliente FROM domicilio d " +
            "LEFT JOIN domicilio_ruta r ON d.id = r.domicilio_id JOIN cliente c ON c.id = d.cliente_id " +
            "WHERE (r.ruta_id != :id OR r.ruta_id IS NULL) and c.empresa_id = :empresaId", nativeQuery = true)
    List<DomicilioProjection> buscarClientesAjenos(Long id, Long empresaId);

    List<Ruta> findAllByEmpresaId(Long id);

}
