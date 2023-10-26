package com.example.aquatrack_backend.service;

import com.example.aquatrack_backend.dto.*;
import com.example.aquatrack_backend.exception.PedidoNoValidoException;
import com.example.aquatrack_backend.exception.RecordNotFoundException;
import com.example.aquatrack_backend.model.*;
import com.example.aquatrack_backend.repo.*;
import com.example.aquatrack_backend.validators.PedidoValidator;
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
  private PedidoValidator pedidoValidator;
  @Autowired
  private TipoPedidoRepo tipoPedidoRepo;
  @Autowired
  private EstadoPedidoRepo estadoPedidoRepo;
  @Autowired
  private DomicilioRepo domicilioRepo;
  @Autowired
  private ClienteRepo clienteRepo;


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
  public PedidoBusquedaDTO getParametrosBusqueda(){

    PedidoBusquedaDTO pedidoBusquedaDTO = new PedidoBusquedaDTO();

    pedidoBusquedaDTO.setTipos(tipoPedidoRepo.findAllActives().stream()
            .map(tipo -> ObjetoGenericoDTO.builder()
                    .id(tipo.getId())
                    .nombre(tipo.getNombreTipoPedido())
                    .build())
            .collect(Collectors.toList()));

    pedidoBusquedaDTO.setEstados(estadoPedidoRepo.findAllActives().stream()
            .map(estado -> ObjetoGenericoDTO.builder()
                    .id(estado.getId())
                    .nombre(estado.getNombreEstadoPedido())
                    .build())
            .collect(Collectors.toList()));

    return pedidoBusquedaDTO;
  }

  @Transactional
  public PedidoFormWebDTO getParametrosPedidoWeb(){

    PedidoFormWebDTO pedidoFormDTO = new PedidoFormWebDTO();
    Long idE = getUsuarioFromContext().getPersona().getEmpresa().getId();

    pedidoFormDTO.setProductos(getProductosParametro(idE));
    pedidoFormDTO.setClientes(clienteRepo.findAllByEmpresa(idE).stream()
            .map(cliente -> ObjetoGenericoDTO.builder()
                    .id(cliente.getId())
                    .nombre(cliente.getNombre())
                    .build())
            .collect(Collectors.toList()));

    return pedidoFormDTO;
  }

  @Transactional
  public PedidoFormMobileDTO getParametrosPedidoMobile(){

    Long idE = getUsuarioFromContext().getPersona().getEmpresa().getId();

    PedidoFormMobileDTO pedidoFormDTO = new PedidoFormMobileDTO();
    pedidoFormDTO.setProductos(getProductosParametro(idE));

    return pedidoFormDTO;
  }

  private List<ObjetoGenericoDTO> getProductosParametro(Long idE){
    return productoRepo.getAllProductos(idE).stream()
            .map(
            producto -> ObjetoGenericoDTO.builder()
                    .id(producto.getId())
                    .nombre(producto.getNombre())
                    .build()
    )
            .collect(Collectors.toList());
  }

  @Transactional
  public PedidoListDTO createPedidoExtraordinarioWeb(GuardarPedidoDTO pedido) throws PedidoNoValidoException {

    Pedido pedidoNuevo = createPedidoExtraordinario(pedido);
    pedidoNuevo.setEstadoPedido(estadoPedidoRepo.findByNombreEstadoPedido("Aprobado"));

    Pedido p = pedidoRepo.save(pedidoNuevo);
    return makePedidoListDTO(p);
  }

  @Transactional
  public PedidoListDTO createPedidoExtraordinarioMobile(GuardarPedidoDTO pedido) throws PedidoNoValidoException{

    Pedido pedidoNuevo = createPedidoExtraordinario(pedido);
    pedidoNuevo.setEstadoPedido(estadoPedidoRepo.findByNombreEstadoPedido("Pendiente de aprobación"));

    Pedido p = pedidoRepo.save(pedidoNuevo);
    return makePedidoListDTO(p);
  }

  private Pedido createPedidoExtraordinario(GuardarPedidoDTO pedido) throws PedidoNoValidoException{

    pedidoValidator.validateCreacionPedido(pedido);

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
    pedidoNuevo.setTipoPedido(tipoPedidoRepo.findByNombreTipoPedido("Extraordinario"));
    pedidoNuevo.setFechaCoordinadaEntrega(pedido.getFechaCoordinadaEntrega());

    /*    if(pedido.getTipo().equalsIgnoreCase("Extraordinario")){
      repartoServicio.crearRepartoAnticipado(pedido.getIdRuta(), pedido.getFechaCoordinadaEntrega(), domicilio);
    }*/

    return pedidoNuevo;
  }

/*  @Transactional
  public PedidoListDTO createPedidoAnticipado(GuardarPedidoAnticipadoDTO pedido, Long idDomicilio) throws RecordNotFoundException{

    Pedido pedidoNuevo = new Pedido();
    Domicilio domicilio = domicilioRepo.findById(idDomicilio).get();
    List<PedidoProducto> pedidoProductos = domicilio.getPedidos().stream()
            .filter(p -> p.getTipoPedido().getId().equals(Long.parseLong("1")) && p.getFechaFinVigencia() == null)
            .findFirst()
            .get()
            .getPedidoProductos();

    List<PedidoProducto> pedidoProductosAnticipado = new ArrayList<>();
    for (PedidoProducto pedidoProducto : pedidoProductos) {
      pedidoProductosAnticipado.add(new PedidoProducto(pedidoProducto.getCantidad(), pedidoNuevo, pedidoProducto.getProducto()));
    }

    pedidoNuevo.setPedidoProductos(pedidoProductosAnticipado);

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
  public PedidoListDTO aprobarPedido(/*AprobarPedidoDTO pedidoRequest,*/ Long idPedido) throws RecordNotFoundException {

    Pedido pedido = pedidoRepo.findById(idPedido).orElseThrow(()-> new RecordNotFoundException("El pedido no fue encontrado"));

/*    if(pedidoRequest.getTipoPedido().equalsIgnoreCase("Extraordinario")) {
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

  @Transactional
  public void cancelarPedido(Long idPedido) throws RecordNotFoundException{
    Pedido pedido = pedidoRepo.findById(idPedido).orElseThrow(()-> new RecordNotFoundException("El pedido no fue encontrado"));

    pedido.setEstadoPedido(estadoPedidoRepo.findByNombreEstadoPedido("Cancelado"));

    pedidoRepo.save(pedido);
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
