package com.example.aquatrack_backend.repo;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.aquatrack_backend.model.TipoEmpleado;

@Repository
public interface TipoEmpleadoRepo extends RepoBase<TipoEmpleado> {

     @Query(value = "SELECT * FROM tipo_empleado WHERE fecha_fin_vigencia IS NULL",
            nativeQuery = true)
     List<TipoEmpleado> findAllActive();
}
