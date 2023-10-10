package com.example.aquatrack_backend.repo;

import com.example.aquatrack_backend.model.DiaDomicilio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface DiaDomicilioRepo extends RepoBase<DiaDomicilio> {

    @Query(value = "DELETE FROM dia_domicilio WHERE id = :id ", nativeQuery = true)
    @Modifying
    void deleteById(Long id);

    @Query(value = "DELETE FROM dia_domicilio WHERE dia_ruta_id = :id ", nativeQuery = true)
    @Modifying
    void deleteByDiaRutaId(Long id);
}
