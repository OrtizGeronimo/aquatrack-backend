package com.example.aquatrack_backend.service;

import com.example.aquatrack_backend.model.Cliente;
import com.example.aquatrack_backend.repo.ClienteRepo;
import com.example.aquatrack_backend.repo.RepoBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClienteServicioImpl extends ServicioBaseImpl<Cliente> implements ServicioBase<Cliente> {

    @Autowired
    private ClienteRepo clienteRepo;

    public ClienteServicioImpl(RepoBase<Cliente> repoBase) {
        super(repoBase);
    }
}
