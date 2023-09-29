package com.example.aquatrack_backend.repo;

import com.example.aquatrack_backend.dto.UbicacionDTO;
import com.example.aquatrack_backend.model.Domicilio;
import com.example.aquatrack_backend.model.Ubicacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DomicilioRepo extends RepoBase<Domicilio> {
    @Query(value = "SELECT * FROM ubicacion u " +
            "INNER JOIN (SELECT * FROM domicilio WHERE id = :idDomicilio) d " +
            "ON u.id = d.ubicacion_id", nativeQuery = true)
    Ubicacion findDomicilioUbi(@Param("idDomicilio") Long idDomicilio);
}
