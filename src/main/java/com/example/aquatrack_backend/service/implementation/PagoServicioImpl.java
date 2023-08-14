package com.example.aquatrack_backend.service.implementation;

import com.example.aquatrack_backend.repo.PagoRepo;
import com.example.aquatrack_backend.service.PagoServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PagoServicioImpl implements PagoServicio {

    @Autowired
    private PagoRepo repo;

}
