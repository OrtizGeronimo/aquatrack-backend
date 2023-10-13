package com.example.aquatrack_backend.service;

import com.example.aquatrack_backend.dto.CodigoDTO;
import com.example.aquatrack_backend.dto.EmpresaDTO;
import com.example.aquatrack_backend.model.CodigoTemporal;
import com.example.aquatrack_backend.model.Empleado;
import com.example.aquatrack_backend.model.Empresa;
import com.example.aquatrack_backend.model.Rol;
import com.example.aquatrack_backend.repo.CodigoTemporalRepo;
import com.example.aquatrack_backend.repo.RepoBase;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
public class CodigoTemporalServicio extends ServicioBaseImpl<CodigoTemporal>{

    @Autowired
    CodigoTemporalRepo codigoTemporalRepo;

    private static final String CARACTERES_PERMITIDOS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int LONGITUD_CODIGO = 6;

    public CodigoTemporalServicio(RepoBase<CodigoTemporal> repoBase) {
        super(repoBase);
    }

    @Transactional
    public CodigoDTO generarCodigoAlta(){
        StringBuilder codigo = new StringBuilder();

        Random random = new Random();
        for (int i = 0; i < LONGITUD_CODIGO; i++) {
            int indice = random.nextInt(CARACTERES_PERMITIDOS.length());
            char caracter = CARACTERES_PERMITIDOS.charAt(indice);
            codigo.append(caracter);
        }

        Empresa empresa = ((Empleado) getUsuarioFromContext().getPersona()).getEmpresa();
        CodigoTemporal codigoTemporal = new CodigoTemporal();
        codigoTemporal.setCodigo(codigo.toString());

        codigoTemporal.setFechaExpiracion(LocalDateTime.now().plusDays(1));
        codigoTemporal.setEmpresa(empresa);
        codigoTemporalRepo.save(codigoTemporal);

        return new ModelMapper().map(codigoTemporal, CodigoDTO.class);
    }

    @Transactional
    public Long obtenerEmpresaPorCodigo(String codigo){
        return codigoTemporalRepo.findEmpresaByCode(codigo);
    }

    @Transactional
    public void eliminarCodigoUtilizado(String codigo){
        codigoTemporalRepo.deleteUsedCode(codigo);
    }

    @Transactional
    @Scheduled(cron = "0 */15 * * * *")
    public void eliminarCodigosExpirados(){
        codigoTemporalRepo.deleteExpiratedCodes();
    }
}
