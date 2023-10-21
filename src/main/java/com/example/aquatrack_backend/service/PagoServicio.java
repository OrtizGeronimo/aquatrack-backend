package com.example.aquatrack_backend.service;

import com.example.aquatrack_backend.dto.PagoDTO;
import com.example.aquatrack_backend.exception.RecordNotFoundException;
import com.example.aquatrack_backend.model.*;
import com.example.aquatrack_backend.repo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class PagoServicio extends ServicioBaseImpl<Pago> {

  @Autowired
  private PagoRepo pagoRepo;
  @Autowired
  private MedioPagoRepo medioPagoRepo;
  @Autowired
  private EstadoPagoRepo estadoPagoRepo;
  @Autowired
  private ClienteRepo clienteRepo;
  @Autowired
  private DeudaServicio deudaServicio;
  @Autowired
  private DeudaRepo deudaRepo;
  @Autowired
  private EntregaRepo entregaRepo;


  public PagoServicio(RepoBase<Pago> repoBase) {
    super(repoBase);
  }


  @Transactional
  public PagoDTO cargarPago(Long idCliente, BigDecimal monto, Long idMedioPago) throws RecordNotFoundException {

    Cliente cliente = clienteRepo.findById(idCliente).orElseThrow(() -> new RecordNotFoundException("No se encontró un cliente con el id " + idCliente));

    Deuda deuda = cliente.getDomicilio().getDeuda();

    return crearPago(deuda, monto, idMedioPago);

  }

  @Transactional
  public PagoDTO cobrar(Long idEntrega, BigDecimal monto, Long idMedioPago) throws RecordNotFoundException {
    Entrega entrega = entregaRepo.findById(idEntrega).orElseThrow(() -> new RecordNotFoundException("No se encontró una entrega con el id " + idEntrega));

    Deuda deuda = entrega.getDomicilio().getDeuda();

    return crearPago(deuda, monto, idMedioPago);

  }

  @Transactional
  public PagoDTO crearPago(Deuda deuda, BigDecimal monto, Long idMedioPago) throws RecordNotFoundException {
    Pago pago = new Pago();
    pago.setTotal(monto);
    pago.setMedioPago(medioPagoRepo.findById(idMedioPago).orElseThrow(() -> new RecordNotFoundException("No se encontró un medio de pago con el id " + idMedioPago)));
    pago.setFechaPago(LocalDateTime.now());
    pago.setEstadoPago(estadoPagoRepo.findByNombre("Aceptado"));
    pagoRepo.save(pago);

    DeudaPago deudaPago = new DeudaPago();
    deudaPago.setPago(pago);
    deudaPago.setMontoAdeudadoPago(pago.getTotal());

    deuda.setFechaUltimaActualizacion(LocalDateTime.now());
    deuda.getDeudaPagos().add(deudaPago);

    deudaRepo.save(deuda);
    deudaServicio.recalcularDeuda(deuda.getId());

    PagoDTO response = new PagoDTO();
    response.setId(pago.getId());
    response.setFechaPago(pago.getFechaPago());
    response.setTotal(pago.getTotal());
    response.setMedioPago(pago.getMedioPago().getNombre());
    return response;
  }

  //verPagosDeCliente
}
