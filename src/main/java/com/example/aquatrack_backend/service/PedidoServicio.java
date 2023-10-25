package com.example.aquatrack_backend.service;

import com.example.aquatrack_backend.dto.*;
import com.example.aquatrack_backend.exception.RecordNotFoundException;
import com.example.aquatrack_backend.model.*;
import com.example.aquatrack_backend.repo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PedidoServicio extends ServicioBaseImpl<Pedido> {

  @Autowired
  private PedidoRepo pedidoRepo;
  @Autowired
  private ProductoRepo productoRepo;
  @Autowired
  private TipoPedidoRepo tipoPedidoRepo;
  @Autowired
  private EstadoPedidoRepo estadoPedidoRepo;
  @Autowired
  private DomicilioRepo domicilioRepo;
  @Autowired
  private RutaRepo rutaRepo;

  @Autowired
  private RepartoServicio repartoServicio;

  public PedidoServicio(RepoBase<Pedido> repoBase) {
    super(repoBase);
  }

  @Transactional
  public Page<PedidoListDTO> getAllPedidos(Integer page, Integer size, boolean mostrar_inactivos, String nombreCliente, Long estadoPedido, Long tipoPedido, String fechaCoordinadaEntregaDesde, String fechaCoordinadaEntregaHasta){
    Empresa empresa = ((Empleado) getUsuarioFromContext().getPersona()).getEmpresa();
    Pageable paging = PageRequest.of(page, size);
    Page<Pedido> pedidos = pedidoRepo.getAllPedidos(empresa.getId(), paging, mostrar_inactivos, nombreCliente, estadoPedido, tipoPedido,
            fechaCoordinadaEntregaDesde == null ? null : LocalDateTime.parse(fechaCoordinadaEntregaDesde),
            fechaCoordinadaEntregaHasta == null ? null : LocalDateTime.parse(fechaCoordinadaEntregaHasta));
    return pedidos.map(this::makePedidoListDTO);
  }

  @Transactional
  public PedidoListDTO createPedido(GuardarPedidoDTO pedido) throws RecordNotFoundException{

    Pedido pedidoNuevo = new Pedido();
    pedidoNuevo.setPedidoProductos(
            pedido.getPedidoProductos().stream().map(
                    pedidoProducto -> new PedidoProducto(pedidoProducto.getCantidad(),
                                                         pedidoNuevo,
                                                         productoRepo.findById(pedidoProducto.getIdProducto()).get()
                                                         )
            ).collect(Collectors.toList())
    );

    Domicilio domicilio = domicilioRepo.findById(pedido.getIdDomicilio()).get();
    pedidoNuevo.setDomicilio(domicilio);
    pedidoNuevo.setTipoPedido(tipoPedidoRepo.findByNombreTipoPedido(pedido.getTipo()));
    pedidoNuevo.setFechaCoordinadaEntrega(pedido.getFechaCoordinadaEntrega());

    /*if(pedido.getTipo().equalsIgnoreCase("Extraordinario")){
      repartoServicio.crearRepartoAnticipado(pedido.getIdRuta(), pedido.getFechaCoordinadaEntrega(), domicilio);
    }*/

    pedidoNuevo.setEstadoPedido(estadoPedidoRepo.findByNombreEstadoPedido("Aprobado"));

    Pedido p = pedidoRepo.save(pedidoNuevo);
    return makePedidoListDTO(p);
  }

  /*@Transactional
  public PedidoListDTO createPedidoAnticipado(GuardarPedidoAnticipadoDTO pedido) throws RecordNotFoundException{

    Pedido pedidoNuevo = new Pedido();
    Domicilio domicilio = domicilioRepo.findById(pedido.getIdDomicilio()).get();
    List<PedidoProducto> pedidoProductos = domicilio.getPedidos().stream()
            .filter(p -> p.getTipoPedido().getId().equals(1) && p.getFechaFinVigencia() == null)
            .findFirst()
            .get()
            .getPedidoProductos();

    pedidoNuevo.setPedidoProductos(pedidoProductos);

    pedidoNuevo.setDomicilio(domicilio);
    pedidoNuevo.setTipoPedido(tipoPedidoRepo.findByNombreTipoPedido("Anticipado"));
    pedidoNuevo.setFechaCoordinadaEntrega(pedido.getFechaCoordinadaEntrega());

    repartoServicio.crearRepartoAnticipado(pedido.getIdRuta(), pedido.getFechaCoordinadaEntrega(), domicilio);

    pedidoNuevo.setEstadoPedido(estadoPedidoRepo.findByNombreEstadoPedido("Aprobado"));

    Pedido p = pedidoRepo.save(pedidoNuevo);
    return makePedidoListDTO(p);
  }*/

  @Transactional
  public PedidoListDTO detallarPedido(Long idPedido) throws RecordNotFoundException {

    Pedido pedido = pedidoRepo.findById(idPedido).orElseThrow(()-> new RecordNotFoundException("El pedido no fue encontrado"));

    return makePedidoListDTO(pedido);
  }

  @Transactional
  public PedidoListDTO aprobarPedido(AprobarPedidoDTO pedidoRequest, Long idPedido) throws RecordNotFoundException {

    Pedido pedido = pedidoRepo.findById(idPedido).orElseThrow(()-> new RecordNotFoundException("El pedido no fue encontrado"));

    /*if(pedidoRequest.getTipoPedido().equalsIgnoreCase("Extraordinario") || pedidoRequest.getTipoPedido().equalsIgnoreCase("Anticipado")) {
      repartoServicio.crearRepartoAnticipado(pedidoRequest.getIdRuta(), pedido.getFechaCoordinadaEntrega(), pedido.getDomicilio());
    }*/

    pedido.setEstadoPedido(estadoPedidoRepo.findByNombreEstadoPedido("Aprobado"));

    pedidoRepo.save(pedido);

    return makePedidoListDTO(pedido);
  }

  @Transactional
  public PedidoListDTO rechazarPedido(Long idPedido) throws RecordNotFoundException {

    Pedido pedido = pedidoRepo.findById(idPedido).orElseThrow(()-> new RecordNotFoundException("El pedido no fue encontrado"));

    pedido.setEstadoPedido(estadoPedidoRepo.findByNombreEstadoPedido("Rechazado"));

    pedidoRepo.save(pedido);

    return makePedidoListDTO(pedido);
  }

  private PedidoListDTO makePedidoListDTO(Pedido pedido){
    return PedidoListDTO.builder()
            .id(pedido.getId())
            .pedidoProductos(pedido.getPedidoProductos()
                    .stream().map(pedidoProducto -> PedidoProductoDTO.builder()
                            .cantidad(pedidoProducto.getCantidad())
                            .nombreProducto(pedidoProducto.getProducto().getNombre())
                            .idProducto(pedidoProducto.getProducto().getId())
                            .build())
                    .collect(Collectors.toList()))
            .domicilio(DomicilioDTO.builder()
                    .domicilio(pedido.getDomicilio().getCalle() + " " +
                            pedido.getDomicilio().getNumero() + " " +
                            pedido.getDomicilio().getPisoDepartamento())
                    .build())
            .fechaCoordinadaEntrega(pedido.getFechaCoordinadaEntrega())
            .estadoPedido(pedido.getEstadoPedido().getNombreEstadoPedido())
            .tipoPedido(pedido.getTipoPedido().getNombreTipoPedido())
            .build();
  }
}
