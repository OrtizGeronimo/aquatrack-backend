package com.example.aquatrack_backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.aquatrack_backend.repo.ClienteRepo;

@Service
public class ClienteServicio extends ServicioBase {

    @Autowired
    private ClienteRepo clienteRepo;

}
