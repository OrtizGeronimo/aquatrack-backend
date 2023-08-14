package com.example.aquatrack_backend.service;

import com.example.aquatrack_backend.repo.RepartoRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RepartoServicioImpl implements ServicioBase {

    @Autowired
    private RepartoRepo repo;
}
