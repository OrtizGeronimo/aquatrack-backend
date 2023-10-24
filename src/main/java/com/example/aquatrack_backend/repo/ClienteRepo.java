package com.example.aquatrack_backend.repo;

import com.example.aquatrack_backend.model.Cliente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClienteRepo extends RepoBase<Cliente> {

    @Query(value = "SELECT * FROM cliente WHERE empresa_id = :empresa_id" +
            " AND (:texto IS NULL or nombre LIKE %:texto% or apellido LIKE %:texto% or num_telefono LIKE %:texto% or dni LIKE %:texto%)" +
            " AND (:mostrar_inactivos = true OR fecha_fin_vigencia IS NULL)",
            nativeQuery = true)
    Page<Cliente> findAllByEmpresaPaged(@Param("empresa_id") Long empresaId, @Param("texto") String texto, @Param("mostrar_inactivos") boolean mostrarInactivos, Pageable pageable);

    @Query(value = "SELECT * FROM cliente c " +
            "WHERE c.dni = :dni", nativeQuery = true)
    Cliente findByDni(@Param("dni")Integer dni);

    @Query(value = "SELECT count(*) as clientes FROM cliente c " +
            "WHERE c.dni = :dni AND c.empresa_id = :empresaId", nativeQuery = true)
    Integer validateUniqueDni(@Param("dni") Integer dni, @Param("empresaId")Long empresaId);

    @Query(value = "SELECT c.id FROM cliente c " +
            "WHERE c.dni = :dni AND c.empresa_id = :empresaId", nativeQuery = true)
    List<Long> validateDniUpdate(@Param("dni") Integer dni, @Param("empresaId")Long empresaId);

    @Query(value = "SELECT id FROM cliente " +
            "WHERE estado_cliente_id = 1" +
            " AND fecha_creacion < DATE_SUB(CURRENT_DATE, INTERVAL 1 DAY)", nativeQuery = true)
    List<Long> findAllUnusedClients();

    @Query(value = "DELETE FROM domicilio " +
            "WHERE cliente_id = :clienteId", nativeQuery = true)
    @Modifying
    void deleteClientDomicily(@Param("clienteId")Long clienteId);
}
