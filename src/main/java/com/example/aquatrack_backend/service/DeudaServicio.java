package com.example.aquatrack_backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.aquatrack_backend.repo.DeudaRepo;

@Service
public class DeudaServicio extends ServicioBase {

    @Autowired
    private DeudaRepo deudaRepo;

}
