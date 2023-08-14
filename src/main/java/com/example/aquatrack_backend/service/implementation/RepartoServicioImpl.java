package com.example.aquatrack_backend.service.implementation;

import com.example.aquatrack_backend.repo.RepartoRepo;
import com.example.aquatrack_backend.service.RepartoServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RepartoServicioImpl implements RepartoServicio {

    @Autowired
    private RepartoRepo repo;

}
