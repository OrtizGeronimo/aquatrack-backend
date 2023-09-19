package com.example.aquatrack_backend.repo;

import com.example.aquatrack_backend.dto.ClienteDTO;
import com.example.aquatrack_backend.model.Cliente;
import com.example.aquatrack_backend.model.Rol;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ClienteRepo extends RepoBase<Cliente> {

    @Query(value = "SELECT * FROM cliente c " +
            "INNER JOIN (SELECT * FROM empresa_cliente WHERE empresa_id = :empresa_id ) ce " +
            "ON ce.cliente_id = c.id",
            nativeQuery = true)
    Page<Cliente> findAllByEmpresa(@Param("empresa_id") Long empresaId, /*@Param("nombre") String nombre, @Param("mostrar_inactivos") boolean mostrarInactivos,*/ Pageable pageable);

    @Query(value = "SELECT c.id FROM cliente c " +
            "WHERE c.dni = :dni", nativeQuery = true)
    Long findByDni(@Param("dni")Integer dni);
}
