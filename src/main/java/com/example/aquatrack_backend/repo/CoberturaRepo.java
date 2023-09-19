package com.example.aquatrack_backend.repo;


import org.springframework.stereotype.Repository;

import com.example.aquatrack_backend.model.Cobertura;

import com.example.aquatrack_backend.model.Cliente;
import com.example.aquatrack_backend.model.Cobertura;
import com.example.aquatrack_backend.model.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface CoberturaRepo extends RepoBase<Cobertura> {
    Optional<Cobertura> findCoberturaByEmpresa(Empresa empresa);
}
