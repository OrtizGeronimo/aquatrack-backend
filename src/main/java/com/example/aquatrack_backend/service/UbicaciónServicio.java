package com.example.aquatrack_backend.service;

import com.example.aquatrack_backend.dto.UbicacionDTO;
import com.example.aquatrack_backend.model.Ubicacion;
import com.example.aquatrack_backend.repo.UbicacionRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UbicaciónServicio {

    @Autowired
    UbicacionRepo ubicacionRepo;

    public UbicacionDTO obtenerUbicacion(Long id) throws Exception{
        try{
            Optional<Ubicacion> ubi = ubicacionRepo.findById(id);
            UbicacionDTO ubicacionDTO = new UbicacionDTO();
            ubicacionDTO.setLatitud(ubi.get().getLatitud());
            ubicacionDTO.setLongitud(ubi.get().getLongitud());
            return ubicacionDTO;
        } catch (Exception e){
            throw new Exception(e.getMessage());
        }
    }

    public UbicacionDTO guardarUbicacion(Ubicacion ubicacion) throws Exception{
        try{
            Ubicacion ubi = ubicacionRepo.save(ubicacion);
            UbicacionDTO ubicacionDTO = new UbicacionDTO();
            ubicacionDTO.setLatitud(ubi.getLatitud());
            ubicacionDTO.setLongitud(ubi.getLongitud());
            return ubicacionDTO;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

}
