package com.example.aquatrack_backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.aquatrack_backend.model.Deuda;
import com.example.aquatrack_backend.repo.DeudaRepo;
import com.example.aquatrack_backend.repo.RepoBase;

@Service
public class DeudaServicio extends ServicioBaseImpl<Deuda> {

    @Autowired
    private DeudaRepo deudaRepo;

    public DeudaServicio(RepoBase<Deuda> repoBase) {
        super(repoBase);
    }
}
