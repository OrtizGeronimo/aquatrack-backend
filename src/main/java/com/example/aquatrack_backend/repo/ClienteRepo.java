package com.example.aquatrack_backend.repo;

import com.example.aquatrack_backend.model.Cliente;
import com.example.aquatrack_backend.model.Empresa;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ClienteRepo extends RepoBase<Cliente> {

    @Query(value = "SELECT * FROM cliente WHERE empresa_id = :empresa_id" +
            " AND (:texto IS NULL or nombre LIKE %:texto% or apellido LIKE %:texto% or num_telefono LIKE %:texto% or dni LIKE %:texto%)" +
            " AND (:mostrar_inactivos = true OR fecha_fin_vigencia IS NULL)", countQuery = "SELECT COUNT(id) FROM cliente",
            nativeQuery = true)
    Page<Cliente> findAllByEmpresaPaged(@Param("empresa_id") Long empresaId, @Param("texto") String texto, @Param("mostrar_inactivos") boolean mostrarInactivos, Pageable pageable);

    @Query(value = "SELECT * FROM cliente c " +
            "WHERE c.dni = :dni", nativeQuery = true)
    Cliente findByDni(@Param("dni") Integer dni);

    @Query(value = "SELECT * FROM cliente WHERE empresa_id = :idEmpresa" +
            " AND fecha_fin_vigencia IS NULL",
            nativeQuery = true)
    List<Cliente> findAllByEmpresa(@Param("idEmpresa") Long empresaId);

    List<Cliente> findClientesByEmpresa(Empresa empresa);

    @Query(value = "SELECT count(*) as clientes FROM cliente c " +
            "WHERE c.dni = :dni AND c.empresa_id = :empresaId", nativeQuery = true)
    Integer validateUniqueDni(@Param("dni") Integer dni, @Param("empresaId") Long empresaId);

    @Query(value = "SELECT c.id FROM cliente c " +
            "WHERE c.dni = :dni AND c.empresa_id = :empresaId", nativeQuery = true)
    List<Long> validateDniUpdate(@Param("dni") Integer dni, @Param("empresaId") Long empresaId);

    @Query(value = "SELECT id FROM cliente " +
            "WHERE estado_cliente_id = 1" +
            " AND fecha_creacion < DATE_SUB(CURRENT_DATE, INTERVAL 1 DAY)", nativeQuery = true)
    List<Long> findAllUnusedClients();

    @Query(value = "DELETE FROM domicilio " +
            "WHERE cliente_id = :clienteId", nativeQuery = true)
    @Modifying
    void deleteClientDomicily(@Param("clienteId") Long clienteId);

    @Query(value = "SELECT count(c.id) FROM cliente c WHERE c.empresa_id = :id_empresa AND c.fecha_creacion <= :fecha_hasta", nativeQuery = true)
    Long cantidadClientes(@Param("fecha_hasta") LocalDate fechaHasta, @Param("id_empresa") Long idEmpresa);
}
