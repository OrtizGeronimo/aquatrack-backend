package com.example.aquatrack_backend.repo;

import com.example.aquatrack_backend.model.Entrega;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EntregaRepo extends RepoBase<Entrega> {

    @Query(value = "SELECT * FROM entrega e " +
            "WHERE e.reparto_id = :idReparto", nativeQuery = true)
    List<Entrega> findAllByReparto(@Param("idReparto") Long idReparto);
}