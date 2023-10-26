package com.example.aquatrack_backend.repo;

import com.example.aquatrack_backend.model.MedioPago;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MedioPagoRepo extends RepoBase<MedioPago> {
    @Query(value = "SELECT mp.* FROM medio_pago mp WHERE fecha_fin_vigencia IS NULL", nativeQuery = true)
    List<MedioPago> findAllMediosPago();

    @Query(value = "SELECT mp.* FROM medio_pago mp", nativeQuery = true)
    List<MedioPago> findAll();
}
