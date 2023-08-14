package com.example.aquatrack_backend.service;

import com.example.aquatrack_backend.repo.ClienteRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClienteServicioImpl implements ServicioBase{

    @Autowired
    private ClienteRepo clienteRepo;
}
