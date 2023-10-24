package com.example.aquatrack_backend.service;

import com.example.aquatrack_backend.dto.PagoDTO;
import com.example.aquatrack_backend.exception.RecordNotFoundException;
import com.example.aquatrack_backend.exception.ValidacionException;
import com.example.aquatrack_backend.model.*;
import com.example.aquatrack_backend.repo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
  public PagoDTO crearPago(Deuda deuda, BigDecimal monto, Long idMedioPago) throws RecordNotFoundException {
    Pago pago = new Pago();
    pago.setTotal(monto);
    pago.setMedioPago(medioPagoRepo.findById(idMedioPago).orElseThrow(() -> new RecordNotFoundException("No se encontró un medio de pago con el id " + idMedioPago)));
    pago.setFechaPago(LocalDateTime.now());
    pago.setEstadoPago(estadoPagoRepo.findByNombre("Aceptado"));
    Empleado empleado = (Empleado) getUsuarioFromContext().getPersona();
    pago.setEmpleado(empleado);
    pagoRepo.save(pago);

    DeudaPago deudaPago = new DeudaPago();
    deudaPago.setPago(pago);
    deudaPago.setDeuda(deuda);
    deudaPago.setMontoAdeudadoPago(pago.getTotal().negate()); //es una resta a la deuda, una disminución, por eso se guarda el valor negativo

    List<DeudaPago> deudaPagos = new ArrayList<>();

    deudaPagos.addAll(deuda.getDeudaPagos());

    deudaPagos.add(deudaPago);

    deuda.setFechaUltimaActualizacion(LocalDateTime.now());
    deuda.getDeudaPagos().clear();
    deuda.getDeudaPagos().addAll(deudaPagos);

    deudaRepo.save(deuda);
    deudaServicio.recalcularDeuda(deuda.getDomicilio().getId());

    PagoDTO response = new PagoDTO();
    response.setId(pago.getId());
    response.setFechaPago(pago.getFechaPago());
    response.setTotal(pago.getTotal());
    response.setMedioPago(pago.getMedioPago().getNombre());
    return response;
  }

  @Transactional
  public PagoDTO cobrar(Long idEntrega, BigDecimal monto, Long idMedioPago) throws RecordNotFoundException, ValidacionException {
    Entrega entrega = entregaRepo.findById(idEntrega).orElseThrow(() -> new RecordNotFoundException("No se encontró una entrega con el id " + idEntrega));

    Deuda deuda = entrega.getDomicilio().getDeuda();

    BigDecimal diferencia = entrega.getMonto().subtract(monto);

    if (diferencia.add(deuda.getMonto()).compareTo(deuda.getMontoMaximo()) > 0){
      throw new ValidacionException("No se puede realizar el pago debido a que sumaría una deuda mayor al monto máximo");
    }

    Pago pago = new Pago();
    pago.setTotal(monto);
    pago.setMedioPago(medioPagoRepo.findById(idMedioPago).orElseThrow(() -> new RecordNotFoundException("No se encontró un medio de pago con el id " + idMedioPago)));
    pago.setFechaPago(LocalDateTime.now());
    pago.setEstadoPago(estadoPagoRepo.findByNombre("Aceptado"));
    Empleado empleado = (Empleado) getUsuarioFromContext().getPersona();
    pago.setEmpleado(empleado);
    entrega.setPago(pago);
    pago.setEntrega(entrega);
    pagoRepo.save(pago);

    DeudaPago deudaPago = new DeudaPago();
    deudaPago.setPago(pago);
    deudaPago.setDeuda(deuda);
    //se crea la deuda pago con la dif entre el precio de la entrega y lo que se pagó
    deudaPago.setMontoAdeudadoPago(diferencia);

    List<DeudaPago> deudaPagos = new ArrayList<>();

    deudaPagos.addAll(deuda.getDeudaPagos());

    deudaPagos.add(deudaPago);

    deuda.setFechaUltimaActualizacion(LocalDateTime.now());
    deuda.getDeudaPagos().clear();
    deuda.getDeudaPagos().addAll(deudaPagos);

    deudaRepo.save(deuda);
    deudaServicio.recalcularDeuda(entrega.getDomicilio().getId());

    PagoDTO response = new PagoDTO();
    response.setId(pago.getId());
    response.setFechaPago(pago.getFechaPago());
    response.setTotal(pago.getTotal());
    response.setMedioPago(pago.getMedioPago().getNombre());
    return response;

  }



  public List<PagoDTO> listarPagosPorCliente(Long idCliente) throws RecordNotFoundException {
    Cliente cliente = clienteRepo.findById(idCliente).orElseThrow(() -> new RecordNotFoundException("No se encontró un cliente con el id " + idCliente));


    List<Pago> pagos = cliente.getDomicilio().getDeuda().getDeudaPagos().stream().map(DeudaPago::getPago).collect(Collectors.toList());

    return pagos.stream().map( pago -> {
      PagoDTO response = new PagoDTO();
      response.setId(pago.getId());
      response.setFechaPago(pago.getFechaPago());
      response.setTotal(pago.getTotal());
      response.setMedioPago(pago.getMedioPago().getNombre());
      response.setIdEntrega(pago.getEntrega() == null ? null : pago.getEntrega().getId());
      return response;
    }).collect(Collectors.toList());
  }

}
