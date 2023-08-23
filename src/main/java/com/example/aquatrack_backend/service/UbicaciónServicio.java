package com.example.aquatrack_backend.service;

import com.example.aquatrack_backend.model.Ubicacion;
import com.example.aquatrack_backend.repo.UbicacionRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UbicaciónServicio {

    @Autowired
    UbicacionRepo ubicacionRepo;

    public Ubicacion obtenerUbicacionUsuario(Long id) throws Exception{
        try{
            Optional<Ubicacion> ubi = ubicacionRepo.findById(id);
            return ubi.get();
        } catch (Exception e){
            throw new Exception(e.getMessage());
        }
    }

    public Ubicacion guardarUbicacion(Ubicacion ubicacion) throws Exception{
        try{
            ubicacion = ubicacionRepo.save(ubicacion);
            return ubicacion;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

}
