package com.example.aquatrack_backend.service.implementation;

import com.example.aquatrack_backend.repo.DeudaRepo;
import com.example.aquatrack_backend.service.DeudaServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeudaServicioImpl implements DeudaServicio {

    @Autowired
    private DeudaRepo repo;

}
