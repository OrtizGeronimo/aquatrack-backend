package com.example.aquatrack_backend.service;

import com.example.aquatrack_backend.model.Pago;
import com.example.aquatrack_backend.model.Producto;
import com.example.aquatrack_backend.repo.PagoRepo;
import com.example.aquatrack_backend.repo.RepoBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PagoServicio extends ServicioBase{

    @Autowired
    private PagoRepo pagoRepo;

}
