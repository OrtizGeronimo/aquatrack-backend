package com.example.aquatrack_backend.service;

import com.example.aquatrack_backend.repo.DomicilioRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DomicilioServicioImpl implements ServicioBase{
    @Autowired
    private DomicilioRepo domicilioRepo;
}
