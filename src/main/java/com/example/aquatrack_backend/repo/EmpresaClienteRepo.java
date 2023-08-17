package com.example.aquatrack_backend.repo;

import com.example.aquatrack_backend.model.EmpresaCliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmpresaClienteRepo extends RepoBase<EmpresaCliente> {
}
