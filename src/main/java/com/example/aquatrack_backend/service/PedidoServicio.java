package com.example.aquatrack_backend.service;

import com.example.aquatrack_backend.dto.*;
import com.example.aquatrack_backend.exception.PedidoNoValidoException;
import com.example.aquatrack_backend.exception.RecordNotFoundException;
import com.example.aquatrack_backend.exception.UserUnauthorizedException;
import com.example.aquatrack_backend.model.*;
import com.example.aquatrack_backend.repo.*;
import com.example.aquatrack_backend.validators.PedidoValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
    public Page<PedidoListDTO> getAllPedidos(Integer page, Integer size, boolean mostrar_inactivos, String nombreCliente, Long estadoPedido, Long tipoPedido, String fechaCoordinadaEntregaDesde, String fechaCoordinadaEntregaHasta) {
        Empresa empresa = ((Empleado) getUsuarioFromContext().getPersona()).getEmpresa();
        Pageable paging = PageRequest.of(page, size);
        Page<Pedido> pedidos = pedidoRepo.getAllPedidos(empresa.getId(), paging, mostrar_inactivos, nombreCliente, estadoPedido, tipoPedido,
                fechaCoordinadaEntregaDesde == null ? null : LocalDateTime.parse(fechaCoordinadaEntregaDesde),
                fechaCoordinadaEntregaHasta == null ? null : LocalDateTime.parse(fechaCoordinadaEntregaHasta));
        return pedidos.map(this::makePedidoListDTO);
    }

    @Transactional
    public PedidoBusquedaDTO getParametrosBusqueda() {

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
    public PedidoFormWebDTO getParametrosPedidoWeb() {

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
    public PedidoFormMobileDTO getParametrosPedidoMobile() {

        Long idE = getUsuarioFromContext().getPersona().getEmpresa().getId();

        PedidoFormMobileDTO pedidoFormDTO = new PedidoFormMobileDTO();
        pedidoFormDTO.setProductos(getProductosParametro(idE));

        return pedidoFormDTO;
    }

    private List<ObjetoGenericoDTO> getProductosParametro(Long idE) {
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
    public PedidoListMobileDTO createPedidoExtraordinarioMobile(GuardarPedidoMobileDTO pedidoCrear) throws PedidoNoValidoException, UserUnauthorizedException {
        Persona persona = getUsuarioFromContext().getPersona();
        if (persona instanceof Empleado) {
            throw new UserUnauthorizedException("Esta funcionalidad es exclusiva para clientes de Aquatrack.");
        }

        Cliente cliente = (Cliente) persona;
        Pedido pedidoNuevo = createPedidoExtraordinario(GuardarPedidoDTO.builder().idDomicilio(cliente.getDomicilio().getId()).pedidoProductos(pedidoCrear.getPedidoProductos()).fechaCoordinadaEntrega(pedidoCrear.getFechaCoordinadaEntrega()).build());
        pedidoNuevo.setEstadoPedido(estadoPedidoRepo.findByNombreEstadoPedido("Pendiente de aprobación"));

        Pedido p = pedidoRepo.save(pedidoNuevo);
        return makePedidoListMobileDTO(p);
    }

    private Pedido createPedidoExtraordinario(GuardarPedidoDTO pedido) throws PedidoNoValidoException {

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

        Pedido pedido = pedidoRepo.findById(idPedido).orElseThrow(() -> new RecordNotFoundException("El pedido no fue encontrado"));

        return makePedidoListDTO(pedido);
    }

    @Transactional
    public PedidoListDTO aprobarPedido(Long idPedido) throws RecordNotFoundException {

        Pedido pedido = pedidoRepo.findById(idPedido).orElseThrow(() -> new RecordNotFoundException("El pedido no fue encontrado"));

        pedido.setEstadoPedido(estadoPedidoRepo.findByNombreEstadoPedido("Aprobado"));

        Pedido p = pedidoRepo.save(pedido);

        return makePedidoListDTO(p);
    }

    @Transactional
    public PedidoListDTO rechazarPedido(Long idPedido) throws RecordNotFoundException {

        Pedido pedido = pedidoRepo.findById(idPedido).orElseThrow(() -> new RecordNotFoundException("El pedido no fue encontrado"));

        pedido.setEstadoPedido(estadoPedidoRepo.findByNombreEstadoPedido("Rechazado"));

        Pedido p = pedidoRepo.save(pedido);

        return makePedidoListDTO(p);
    }

    @Transactional
    public PedidoListDTO cancelarPedido(Long idPedido) throws RecordNotFoundException {
        Pedido pedido = pedidoRepo.findById(idPedido).orElseThrow(() -> new RecordNotFoundException("El pedido no fue encontrado"));

        pedido.setEstadoPedido(estadoPedidoRepo.findByNombreEstadoPedido("Cancelado"));

        Pedido p = pedidoRepo.save(pedido);
        return makePedidoListDTO(p);
    }

    private PedidoListDTO makePedidoListDTO(Pedido pedido) {
        return PedidoListDTO.builder()
                .id(pedido.getId())
                .entregable(pedido.getEntregaPedidos() != null && pedido.getEntregaPedidos().stream().anyMatch(e -> e.getEntrega().getEstadoEntrega().getId() == 1L || e.getEntrega().getEstadoEntrega().getId() == 2L))
                .pedidoProductos(pedido.getPedidoProductos()
                        .stream().map(pedidoProducto -> PedidoProductoDTO.builder()
                                .cantidad(pedidoProducto.getCantidad())
                                .nombreProducto(pedidoProducto.getProducto().getNombre())
                                .idProducto(pedidoProducto.getProducto().getId())
                                .build())
                        .collect(Collectors.toList()))
                .domicilio(DomicilioDTO.builder()
                        .domicilio(formatAddress(pedido.getDomicilio().getCalle(), pedido.getDomicilio().getNumero(), pedido.getDomicilio().getPisoDepartamento(), pedido.getDomicilio().getLocalidad()))
                        .nombreApellidoCliente(pedido.getDomicilio().getCliente().getNombre() + " " + pedido.getDomicilio().getCliente().getApellido())
                        .build())
                .fechaCoordinadaEntrega(pedido.getFechaCoordinadaEntrega())
                .totalPedido(pedido.getPedidoProductos().stream().map(pp -> BigDecimal.valueOf(pp.getCantidad()).multiply(pp.getProducto().getPrecios().stream().filter(pr -> pr.getFechaFinVigencia() == null).findFirst().get().getPrecio())).collect(Collectors.toList()).stream().reduce(BigDecimal.ZERO, BigDecimal::add))
                .estadoPedido(pedido.getEstadoPedido() != null ? pedido.getEstadoPedido().getNombreEstadoPedido() : "")
                .tipoPedido(pedido.getTipoPedido().getNombreTipoPedido())
                .fechaFinVigencia(pedido.getFechaFinVigencia())
                .build();
    }

    private PedidoListMobileDTO makePedidoListMobileDTO(Pedido pedido) {
        return PedidoListMobileDTO.builder()
                .id(pedido.getId())
                .entregable(pedido.getEntregaPedidos() != null && pedido.getEntregaPedidos().stream().anyMatch(e -> e.getEntrega().getEstadoEntrega().getId() == 1L || e.getEntrega().getEstadoEntrega().getId() == 2L))
                .fechaHoraEntrega(pedido.getEntregaPedidos() != null ? getFechaHoraVisita(pedido.getEntregaPedidos().stream().filter(e -> e.getEntrega().getEstadoEntrega().getId() == 3L).findFirst()) : null)
                .productos(pedido.getPedidoProductos()
                        .stream().map(pedidoProducto -> PedidoProductoDTO.builder()
                                .cantidad(pedidoProducto.getCantidad())
                                .precio(pedidoProducto.getProducto().getPrecios().stream().filter(pr -> pr.getFechaFinVigencia() == null).findFirst().get().getPrecio())
                                .nombreProducto(pedidoProducto.getProducto().getNombre())
                                .idProducto(pedidoProducto.getProducto().getId())
                                .build())
                        .collect(Collectors.toList()))
                .fechaCoordinadaEntrega(pedido.getFechaCoordinadaEntrega())
                .precio(pedido.getPedidoProductos().stream().map(pp -> BigDecimal.valueOf(pp.getCantidad()).multiply(pp.getProducto().getPrecios().stream().filter(pr -> pr.getFechaFinVigencia() == null).findFirst().get().getPrecio())).collect(Collectors.toList()).stream().reduce(BigDecimal.ZERO, BigDecimal::add))
                .estadoPedido(pedido.getEstadoPedido() != null ? pedido.getEstadoPedido().getNombreEstadoPedido() : "")
                .tipoPedido(pedido.getTipoPedido().getNombreTipoPedido())
                .fechaFinVigencia(pedido.getFechaFinVigencia())
                .build();
    }

    private LocalDateTime getFechaHoraVisita(Optional<EntregaPedido> entrega) {
        return entrega.map(entregaPedido -> entregaPedido.getEntrega().getFechaHoraVisita()).orElse(null);
    }

    private static String formatAddress(String calle, Integer numero, String piso, String localidad) {
        StringBuilder formattedAddress = new StringBuilder(calle);

        if (numero != null) {
            formattedAddress.append(" ").append(numero);
        }

        if (piso != null && !piso.isEmpty()) {
            formattedAddress.append(" ").append(piso);
        }

        if (localidad != null) {
            formattedAddress.append(", ").append(localidad);
        }

        return formattedAddress.toString();
    }

    public List<PedidoExtraordinarioDomicilioDTO> getDomicilios() {
        Empresa empresa = ((Empleado) getUsuarioFromContext().getPersona()).getEmpresa();
        List<Cliente> clientes = clienteRepo.findAllByEmpresa(empresa.getId());
        List<PedidoExtraordinarioDomicilioDTO> domicilios = new ArrayList<>();
        for (Cliente cliente : clientes) {
            PedidoExtraordinarioDomicilioDTO domicilio = new PedidoExtraordinarioDomicilioDTO();
            domicilio.setId(cliente.getDomicilio().getId());
            domicilio.setCliente(cliente.getNombre() + " " + cliente.getApellido());
            domicilio.setDiaDomicilios(cliente.getDomicilio().getDiaDomicilios().stream().map(dd -> dd.getDiaRuta().getDiaSemana().getId()).collect(Collectors.toList()));
            domicilios.add(domicilio);
        }
        return domicilios;
    }

    public List<PedidoProductoDTO> listarProductos(Long idPedido) throws RecordNotFoundException {
        Pedido pedido = pedidoRepo.findById(idPedido).orElseThrow(() -> new RecordNotFoundException("El pedido no fue encontrado"));

        List<PedidoProductoDTO> productos = new ArrayList<>();
        if (!pedido.getPedidoProductos().isEmpty()) {
            for (PedidoProducto producto : pedido.getPedidoProductos()) {
                productos.add(PedidoProductoDTO.builder().idProducto(producto.getProducto().getId()).nombreProducto(producto.getProducto().getNombre()).cantidad(producto.getCantidad()).precio(producto.getProducto().getPrecios().stream().filter(pr -> pr.getFechaFinVigencia() == null).findFirst().get().getPrecio()).build());
            }
        }

        return productos;
    }

    public List<PedidoListMobileDTO> getAllPedidosMobile(Long estadoPedido, Long tipoPedido, LocalDate fechaCoordinadaEntrega) throws UserUnauthorizedException {
        Persona persona = getUsuarioFromContext().getPersona();
        if (persona instanceof Empleado) {
            throw new UserUnauthorizedException("Esta funcionalidad es exclusiva para clientes de Aquatrack.");
        }

        Cliente cliente = (Cliente) persona;
        return pedidoRepo.pedidosCliente(cliente.getDomicilio().getId(), estadoPedido, tipoPedido, fechaCoordinadaEntrega)
                .stream()
                .map(this::makePedidoListMobileDTO).collect(Collectors.toList());
    }

    public PedidoListMobileDTO cancelarPedidoMobile(Long idPedido) throws UserUnauthorizedException, RecordNotFoundException {
        Persona persona = getUsuarioFromContext().getPersona();
        if (persona instanceof Empleado) {
            throw new UserUnauthorizedException("Esta funcionalidad es exclusiva para clientes de Aquatrack.");
        }

        Pedido p = pedidoRepo.findById(idPedido).orElseThrow(() -> new RecordNotFoundException("El pedido no fue encontrado"));

        p.setEstadoPedido(estadoPedidoRepo.findByNombreEstadoPedido("Cancelado"));

        Pedido pp = pedidoRepo.save(p);
        return makePedidoListMobileDTO(pp);
    }
}
