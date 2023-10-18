package com.example.aquatrack_backend.repo;

import com.example.aquatrack_backend.model.DiaRuta;
import com.example.aquatrack_backend.model.Ruta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiaRutaRepo extends RepoBase<DiaRuta> {

    @Query(value = "SELECT * FROM dia_ruta WHERE ruta_id = :rutaId", nativeQuery = true)
    List<DiaRuta> findByRouteId(@Param("rutaId")Long idRuta);
}
