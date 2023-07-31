package com.example.aquatrack_backend.service.implementation;

import com.example.aquatrack_backend.repo.ClienteRepo;
import com.example.aquatrack_backend.service.ClienteServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClienteServicioImpl implements ClienteServicio {

    @Autowired
    private ClienteRepo repo;


}
