package com.example.aquatrack_backend.service;

import com.example.aquatrack_backend.dto.DeudaDTO;
import com.example.aquatrack_backend.dto.DeudaPagoDTO;
import com.example.aquatrack_backend.exception.RecordNotFoundException;
import com.example.aquatrack_backend.model.Cliente;
import com.example.aquatrack_backend.model.Deuda;
import com.example.aquatrack_backend.model.DeudaPago;
import com.example.aquatrack_backend.model.Domicilio;
import com.example.aquatrack_backend.repo.ClienteRepo;
import com.example.aquatrack_backend.repo.DeudaRepo;
import com.example.aquatrack_backend.repo.DomicilioRepo;
import com.example.aquatrack_backend.repo.RepoBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.stream.Collectors;

@Service
public class DeudaServicio extends ServicioBaseImpl<Deuda> {

    @Autowired
    private DeudaRepo deudaRepo;
    @Autowired
    private ClienteRepo clienteRepo;
    @Autowired
    private DomicilioRepo domicilioRepo;

    public DeudaServicio(RepoBase<Deuda> repoBase) {
        super(repoBase);
    }

    public DeudaDTO detalleDeudaMobile() {
        Deuda deudaActual = ((Cliente) getUsuarioFromContext().getPersona()).getDomicilio().getDeuda();

        DeudaDTO response = mapearDeuda(deudaActual);


        return response;
    }

    public DeudaDTO detalleDeuda(Long id) throws RecordNotFoundException {
        Cliente cliente = clienteRepo.findById(id).orElseThrow(() -> new RecordNotFoundException("No se encontró un cliente con el id " + id));

        Deuda deudaCliente = cliente.getDomicilio().getDeuda();

        DeudaDTO response = mapearDeuda(deudaCliente);

        return response;
    }

    /*

    DEUDA NUMERO POSITIVO: AUMENTO DE DEUDA (pagó menos de lo que debería)
    DEUDA NUMERO NEGATIVO: DISMINUCION DE DEUDA (pagó más de lo que debería o se cargó un pago)

    */
    @Transactional
    public void recalcularDeuda(Long idDomicilio) throws RecordNotFoundException {

        Domicilio domicilio = domicilioRepo.findById(idDomicilio).orElseThrow(() -> new RecordNotFoundException("No se encontró un domicilio con el id " + idDomicilio));

        Deuda deuda = domicilio.getDeuda();/*deudaRepo.findById(id).orElseThrow(() -> new RecordNotFoundException("No se encontró una deuda con id " + id));*/

        BigDecimal nuevoMonto = BigDecimal.ZERO;

        for (DeudaPago deudaPago : deuda.getDeudaPagos()) {
            if (deudaPago.getPago().getFechaFinVigencia() == null) {
                nuevoMonto = nuevoMonto.add(deudaPago.getMontoAdeudadoPago());
            }
        }

        deuda.setMonto(nuevoMonto);

        deudaRepo.save(deuda);
    }


    private DeudaDTO mapearDeuda(Deuda deuda) {
        DeudaDTO response = new DeudaDTO();

        response.setId(deuda.getId());
        response.setMonto(deuda.getMonto());
        response.setFechaUltimaActualizacion(deuda.getFechaUltimaActualizacion());

        response.setCambios(deuda.getDeudaPagos().stream().filter(deudaPago -> deudaPago.getPago().getFechaFinVigencia() == null).map(deudaPago -> {
            DeudaPagoDTO dto = new DeudaPagoDTO();
            dto.setMonto(deudaPago.getMontoAdeudadoPago());
            dto.setId(deudaPago.getId());
            dto.setFechaRegistro(deudaPago.getPago().getFechaPago());
            dto.setPago(deudaPago.getPago().getId());
            return dto;
        }).collect(Collectors.toList()));

        return response;
    }


}
