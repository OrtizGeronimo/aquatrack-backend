package com.example.aquatrack_backend.service;

import com.example.aquatrack_backend.dto.*;
import com.example.aquatrack_backend.exception.EntidadNoValidaException;
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
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EntregaServicio extends ServicioBaseImpl<Entrega> {

    @Autowired
    private EntregaRepo entregaRepo;
    @Autowired
    private RepartoRepo repartoRepo;
    @Autowired
    private EstadoEntregaRepo estadoEntregaRepo;
    @Autowired
    private PedidoProductoRepo pedidoProductoRepo;
    @Autowired
    private PedidoRepo pedidoRepo;
    @Autowired
    private TipoPedidoRepo tipoPedidoRepo;
    @Autowired
    private ProductoRepo productoRepo;
    @Autowired
    private DomicilioProductoRepo domicilioProductoRepo;
    @Autowired
    private EntregaDetalleRepo entregaDetalleRepo;
    @Autowired
    private PagoServicio pagoServicio;

    public EntregaServicio(RepoBase<Entrega> repoBase) {
        super(repoBase);
    }

    @Transactional
    public List<EntregaListDTO> findAllEntregasByReparto(Long idReparto) throws RecordNotFoundException {
        Reparto reparto = repartoRepo.findById(idReparto).orElseThrow(() -> new RecordNotFoundException("No se encontró el reparto"));
        return entregaRepo.findAllByReparto(reparto.getId()).stream().map(entrega -> EntregaListDTO.builder().id(entrega.getId()).fechaHoraVisita(entrega.getFechaHoraVisita()).estadoEntregaId(entrega.getEstadoEntrega().getId()).ordenVisita(entrega.getOrdenVisita()).latitudDomicilio(entrega.getDomicilio().getUbicacion().getLatitud()).longitudDomicilio(entrega.getDomicilio().getUbicacion().getLongitud()).estadoEntrega(entrega.getEstadoEntrega().getNombreEstadoEntrega()).cliente(entrega.getDomicilio().getCliente().getNombre() + " " + entrega.getDomicilio().getCliente().getApellido()).build()).collect(Collectors.toList());
    }

    @Transactional
    public EntregaListDTO disableEntrega(Long idEntrega) throws RecordNotFoundException {
        Entrega entrega = entregaRepo.findById(idEntrega).orElseThrow(() -> new RecordNotFoundException("No se encontró la entrega"));
        entrega.setEstadoEntrega(estadoEntregaRepo.findByNombreEstadoEntrega("Cancelada"));
        entregaRepo.save(entrega);
        return EntregaListDTO.builder().id(entrega.getId()).build();
    }

    @Transactional(rollbackFor = {EntidadNoValidaException.class, RecordNotFoundException.class, ValidacionException.class})
    public void procesarEntrega(Long idEntrega, ProcesarEntregaDTO entregaProcesada) throws RecordNotFoundException, ValidacionException, EntidadNoValidaException {
        Entrega entrega = entregaRepo.findById(idEntrega).orElseThrow(() -> new RecordNotFoundException("No se encontró la entrega"));
        if (entrega.getEstadoEntrega().getNombreEstadoEntrega().equals("Procesada")) {
            throw new ValidacionException("No se puede procesar una entrega que ya esta procesada.");
        }

        List<EntregaDetalle> entregaDetalles = new ArrayList<>();

        for (PedidoEntregadoDTO pedidoEntregado : entregaProcesada.getPedidosEntregados()) {
            Pedido pedido = pedidoRepo.findById(pedidoEntregado.getId()).get();
            TipoPedido pedidoHabitual = tipoPedidoRepo.findByNombreTipoPedido("Habitual");
            if (pedido.getTipoPedido().equals(pedidoHabitual)) {
                if (pedido.getDomicilio().getProductoDomicilios().isEmpty()) {
                    /*
                        Si no hay productos en el domicilio los creamos
                    */
                    for (PedidoEntregadoProductoDTO producto : pedidoEntregado.getProductos()) {
                        PedidoProducto pp = pedido.getPedidoProductos().stream().filter(p -> p.getProducto().getId() == producto.getId()).findFirst().get();
                        if (productoRepo.findProductoById(producto.getId()).getRetornable()) {
                            DomicilioProducto dp = new DomicilioProducto();
                            dp.setDomicilio(pedido.getDomicilio());
                            dp.setProducto(productoRepo.findProductoById(producto.getId()));
                            if (producto.getEntregado() > pp.getCantidad()) {
                                dp.setCantidadDevolver(producto.getEntregado() - pp.getCantidad());
                            }
                            dp.setCantidad(producto.getEntregado());
                            domicilioProductoRepo.save(dp);
                        }
                    }
                } else {
                    for (PedidoEntregadoProductoDTO producto : pedidoEntregado.getProductos()) {
                        PedidoProducto pp = pedido.getPedidoProductos().stream().filter(p -> p.getProducto().getId() == producto.getId()).findFirst().get();
                        if (!pedido.getDomicilio().getProductoDomicilios().stream().map(dp -> dp.getProducto().getId()).collect(Collectors.toList()).contains(producto.getId())) {
                            /*
                                Si no está el producto en el domicilio,lo creamos
                            */
                            if (productoRepo.findProductoById(producto.getId()).getRetornable()) {
                                DomicilioProducto dp = new DomicilioProducto();
                                dp.setDomicilio(pedido.getDomicilio());
                                dp.setProducto(productoRepo.findProductoById(producto.getId()));
                                if (producto.getEntregado() > pp.getCantidad()) {
                                    dp.setCantidadDevolver(producto.getEntregado() - pp.getCantidad());
                                }
                                dp.setCantidad(producto.getEntregado());
                                domicilioProductoRepo.save(dp);
                            }
                        } else {
                            if (productoRepo.findProductoById(producto.getId()).getRetornable()) {
                                DomicilioProducto dp = pedido.getDomicilio().getProductoDomicilios().stream().filter(d -> d.getProducto().getId().equals(producto.getId())).findFirst().get();

                                if (producto.getRecibido() > dp.getCantidad()) {
                                    throw new ValidacionException("La cantidad recibida de: " + dp.getProducto().getNombre() + " supera la cantidad que tiene el cliente en su domicilio.");
                                }

                                if (producto.getEntregado() > dp.getCantidad() && producto.getEntregado() > pp.getCantidad()) {
                                    dp.setCantidadDevolver(producto.getEntregado() - dp.getCantidad());
                                    dp.setCantidad(producto.getEntregado());
                                }

                                if (producto.getEntregado() > dp.getCantidad() && producto.getEntregado() <= pp.getCantidad()) {
                                    dp.setCantidad(producto.getEntregado());
                                }

                                if (producto.getRecibido() > producto.getEntregado()) {
                                    dp.setCantidad(dp.getCantidad() - producto.getEntregado());
                                }
                                domicilioProductoRepo.save(dp);
                            }
                        }
                    }
                }
            } else {
                if (pedido.getDomicilio().getProductoDomicilios().isEmpty()) {
                    /*
                        Si no hay productos en el domicilio los creamos
                    */
                    for (PedidoEntregadoProductoDTO producto : pedidoEntregado.getProductos()) {
                        PedidoProducto pp = pedido.getPedidoProductos().stream().filter(p -> p.getProducto().getId() == producto.getId()).findFirst().get();
                        if (productoRepo.findProductoById(producto.getId()).getRetornable()) {
                            DomicilioProducto dp = new DomicilioProducto();
                            dp.setDomicilio(pedido.getDomicilio());
                            dp.setProducto(productoRepo.findProductoById(producto.getId()));
                            if (producto.getEntregado() > pp.getCantidad()) {
                                dp.setCantidadDevolver(producto.getEntregado() - pp.getCantidad());
                            }
                            dp.setCantidad(producto.getEntregado());
                            domicilioProductoRepo.save(dp);
                        }
                    }
                } else {
                    for (PedidoEntregadoProductoDTO producto : pedidoEntregado.getProductos()) {
                        PedidoProducto pp = pedido.getPedidoProductos().stream().filter(p -> p.getProducto().getId() == producto.getId()).findFirst().get();
                        if (producto.getEntregado() > pp.getCantidad()) {
                            HashMap<String, String> errors = new HashMap<>();
                            errors.put("pedidosEntregados.root", "En un pedido extraordinario, no puede entregar mas productos de lo que se pidieron.");
                            throw new EntidadNoValidaException(errors);
                        }

                        if (!pedido.getDomicilio().getProductoDomicilios().stream().map(dp -> dp.getProducto().getId()).collect(Collectors.toList()).contains(producto.getId())) {
                            /*
                                Si no está el producto en el domicilio,lo creamos
                            */
                            if (productoRepo.findProductoById(producto.getId()).getRetornable()) {
                                DomicilioProducto dp = new DomicilioProducto();
                                dp.setDomicilio(pedido.getDomicilio());
                                dp.setProducto(productoRepo.findProductoById(producto.getId()));
                                dp.setCantidad(producto.getEntregado());
                                dp.setCantidadDevolver(producto.getEntregado());
                                domicilioProductoRepo.save(dp);
                            }
                        } else {
                            if (productoRepo.findProductoById(producto.getId()).getRetornable()) {
                                DomicilioProducto dp = pedido.getDomicilio().getProductoDomicilios().stream().filter(d -> d.getProducto().getId().equals(producto.getId())).findFirst().get();
                                if (dp.getCantidadDevolver() == null) {
                                    dp.setCantidadDevolver(producto.getEntregado());
                                } else {
                                    dp.setCantidadDevolver(dp.getCantidadDevolver() + producto.getEntregado());
                                }
                                domicilioProductoRepo.save(dp);
                            }
                        }
                    }
                }
            }

            for (PedidoEntregadoProductoDTO producto : pedidoEntregado.getProductos()) {
                Optional<EntregaDetalle> ed = entrega.getEntregaDetalles().stream().filter(e -> e.getProducto().getId().equals(producto.getId())).findFirst();
                if (ed.isPresent()) {
                    ed.get().setCantidadEntregada(ed.get().getCantidadEntregada() + producto.getEntregado());
                    ed.get().setCantidadRecibida(ed.get().getCantidadRecibida() + producto.getRecibido());
                    entregaDetalles.add(ed.get());
                    entregaDetalleRepo.save(ed.get());
                } else {
                    EntregaDetalle edNuevo = new EntregaDetalle();
                    edNuevo.setCantidadRecibida(producto.getRecibido());
                    edNuevo.setCantidadEntregada(producto.getEntregado());
                    edNuevo.setEntrega(entrega);
                    edNuevo.setProducto(productoRepo.findProductoById(producto.getId()));
                    entregaDetalles.add(edNuevo);
                    entregaDetalleRepo.save(edNuevo);
                }
            }
        }

        if (entregaProcesada.getProductosDevueltos() != null && !entregaProcesada.getProductosDevueltos().isEmpty()) {
            for (PedidoEntregaDevueltoDTO producto : entregaProcesada.getProductosDevueltos()) {
                Optional<DomicilioProducto> dp = entrega.getDomicilio().getProductoDomicilios().stream().filter(d -> d.getProducto().getId().equals(producto.getId())).findFirst();
                if (dp.isPresent()) {
                    if (producto.getEntregado() > dp.get().getCantidadDevolver()) {
                        HashMap<String, String> errors = new HashMap<>();
                        errors.put("productosDevueltos.root", "No puede devolver mas de lo que debe.");
                        throw new EntidadNoValidaException(errors);
                    }

                    dp.get().setCantidadDevolver(dp.get().getCantidadDevolver() - producto.getEntregado());
                    domicilioProductoRepo.save(dp.get());
                }
            }
        }

        entrega.setEntregaDetalles(entregaDetalles);
        BigDecimal monto = BigDecimal.ZERO;
        for (EntregaDetalle ed : entrega.getEntregaDetalles()) {
            monto = monto.add(BigDecimal.valueOf(ed.getCantidadEntregada()).multiply(ed.getProducto().getPrecios().stream().filter(pr -> pr.getFechaFinVigencia() == null).findFirst().get().getPrecio()));
        }
        entrega.setMonto(monto);
        entrega.setFechaHoraVisita(LocalDateTime.now());
        entrega.setObservaciones(entregaProcesada.getObservaciones());
        entrega.setEstadoEntrega(estadoEntregaRepo.findByNombreEstadoEntrega("Procesada"));
        entregaRepo.save(entrega);

        pagoServicio.cobrar(entrega.getId(), entregaProcesada.getPago().getMonto(), entregaProcesada.getPago().getIdMedioPago());
    }

    public List<PedidoProductoDTO> productosEntregar(Long idEntrega) throws RecordNotFoundException {
        Entrega entrega = entregaRepo.findById(idEntrega).orElseThrow(() -> new RecordNotFoundException("No se encontró la entrega"));


        if (entrega.getDomicilio().getProductoDomicilios().isEmpty()) {
            List<PedidoProductoProjection> allProducts = pedidoProductoRepo.getAllPedidoProductos(entrega.getId());
            return allProducts.stream().map(pp -> PedidoProductoDTO.builder().idProducto(pp.getId()).nombreProducto(pp.getNombre()).cantidad(pp.getCantidad()).precio(pp.getPrecio()).build()).collect(Collectors.toList());
        }

        return new ArrayList<>();
    }

    public List<PedidoProductoDTO> productosDomicilio(Long idEntrega) throws RecordNotFoundException {
        Entrega entrega = entregaRepo.findById(idEntrega).orElseThrow(() -> new RecordNotFoundException("No se encontró la entrega"));


        if (!entrega.getDomicilio().getProductoDomicilios().isEmpty()) {
            return entrega.getDomicilio().getProductoDomicilios().stream().map(dp -> PedidoProductoDTO.builder().idProducto(dp.getProducto().getId()).nombreProducto(dp.getProducto().getNombre()).cantidad(dp.getCantidad()).cantidadDevolver(dp.getCantidadDevolver()).precio(dp.getProducto().getPrecios().stream().filter(pr -> pr.getFechaFinVigencia() == null).findFirst().get().getPrecio()).build()).collect(Collectors.toList());
        }

        return new ArrayList<>();
    }

    public void entregaAusente(Long idEntrega, EntregaAusenteDTO observaciones) throws RecordNotFoundException, ValidacionException {
        Entrega entrega = entregaRepo.findById(idEntrega).orElseThrow(() -> new RecordNotFoundException("No se encontró la entrega"));
        if (!entrega.getEstadoEntrega().getNombreEstadoEntrega().equals(estadoEntregaRepo.findByNombreEstadoEntrega("Pendiente").getNombreEstadoEntrega())) {
            throw new ValidacionException("No se puede realizar esta acción si la entrega no está pendiente.");
        }
        EstadoEntrega ausente = estadoEntregaRepo.findByNombreEstadoEntrega("Ausente");

        entrega.setObservaciones(observaciones.getObservaciones());
        entrega.setEstadoEntrega(ausente);
        entrega.setFechaHoraVisita(LocalDateTime.now());
        entregaRepo.save(entrega);
    }

    public List<PedidoEntregaDTO> getEntregaPedidos(Long idEntrega) throws RecordNotFoundException {
        Entrega entrega = entregaRepo.findById(idEntrega).orElseThrow(() -> new RecordNotFoundException("No se encontró la entrega"));
        List<PedidoEntregaDTO> pedidos = new ArrayList<>();

        for (EntregaPedido pedido : entrega.getEntregaPedidos()) {
            List<PedidoProductoProjection> productos = pedidoProductoRepo.getPedidoProductos(pedido.getPedido().getId());
            pedidos.add(PedidoEntregaDTO.builder().tipo(pedido.getPedido().getTipoPedido().getNombreTipoPedido()).id(pedido.getPedido().getId()).pedidoProductos(productos.stream().map(pp -> PedidoProductoDTO.builder().retornable(pp.getRetornable()).idProducto(pp.getId()).nombreProducto(pp.getNombre()).cantidad(pp.getCantidad()).precio(pp.getPrecio()).build()).collect(Collectors.toList())).build());
        }

        return pedidos;
    }

    public EntregaMobileDTO detallarEntregaMobile(Long id) throws RecordNotFoundException {
        Entrega entrega = entregaRepo.findById(id).orElseThrow(() -> new RecordNotFoundException("No se encontró la entrega"));

        if (entrega.getEstadoEntrega().getNombreEstadoEntrega().equals("Programada") || entrega.getEstadoEntrega().getNombreEstadoEntrega().equals("Pendiente")) {
            return EntregaMobileDTO.builder().repartoId(entrega.getReparto().getId()).fechaEjecucion(entrega.getReparto().getFechaEjecucion()).id(entrega.getId()).ordenVisita(entrega.getOrdenVisita()).observaciones(entrega.getDomicilio().getObservaciones()).nombreCliente(entrega.getDomicilio().getCliente().getNombre() + " " + entrega.getDomicilio().getCliente().getApellido()).domicilio(formatAddress(entrega.getDomicilio().getCalle(), entrega.getDomicilio().getNumero(), entrega.getDomicilio().getPisoDepartamento()) + ", " + entrega.getDomicilio().getLocalidad()).montoRecaudar(entregaRepo.getMontoTotalByEntrega(entrega.getId())).estado(entrega.getEstadoEntrega().getNombreEstadoEntrega()).build();
        }

        if (entrega.getEstadoEntrega().getNombreEstadoEntrega().equals("Cancelada")) {
            return EntregaMobileDTO.builder().repartoId(entrega.getReparto().getId()).fechaEjecucion(entrega.getReparto().getFechaEjecucion()).id(entrega.getId()).ordenVisita(entrega.getOrdenVisita()).observacionesEntrega(entrega.getObservaciones()).nombreCliente(entrega.getDomicilio().getCliente().getNombre() + " " + entrega.getDomicilio().getCliente().getApellido()).domicilio(formatAddress(entrega.getDomicilio().getCalle(), entrega.getDomicilio().getNumero(), entrega.getDomicilio().getPisoDepartamento()) + ", " + entrega.getDomicilio().getLocalidad()).montoRecaudar(entregaRepo.getMontoTotalByEntrega(entrega.getId())).estado(entrega.getEstadoEntrega().getNombreEstadoEntrega()).build();
        }

        if (entrega.getEstadoEntrega().getNombreEstadoEntrega().equals("Ausente")) {
            return EntregaMobileDTO.builder().repartoId(entrega.getReparto().getId()).fechaHoraVisita(entrega.getFechaHoraVisita()).id(entrega.getId()).ordenVisita(entrega.getOrdenVisita()).observacionesEntrega(entrega.getObservaciones()).nombreCliente(entrega.getDomicilio().getCliente().getNombre() + " " + entrega.getDomicilio().getCliente().getApellido()).domicilio(formatAddress(entrega.getDomicilio().getCalle(), entrega.getDomicilio().getNumero(), entrega.getDomicilio().getPisoDepartamento()) + ", " + entrega.getDomicilio().getLocalidad()).montoRecaudar(entregaRepo.getMontoTotalByEntrega(entrega.getId())).estado(entrega.getEstadoEntrega().getNombreEstadoEntrega()).build();
        }

        EntregaMobileDTO response = new EntregaMobileDTO();
        response.setId(entrega.getId());
        response.setRepartoId(entrega.getReparto().getId());
        response.setFechaHoraVisita(entrega.getFechaHoraVisita());
        response.setEstado(entrega.getEstadoEntrega().getNombreEstadoEntrega());
        response.setDomicilio(formatAddress(entrega.getDomicilio().getCalle(), entrega.getDomicilio().getNumero(), entrega.getDomicilio().getPisoDepartamento()) + ", " + entrega.getDomicilio().getLocalidad());
        response.setNombreCliente(entrega.getDomicilio().getCliente().getNombre() + " " + entrega.getDomicilio().getCliente().getApellido());
        response.setMontoEntregado(entrega.getMonto());
        if (entrega.getPago() != null) {
            response.setMontoRecaudado(entrega.getPago().getTotal());
            response.setMedioPago(entrega.getPago().getMedioPago().getNombre());
        }
        response.setObservacionesEntrega(entrega.getObservaciones());
        return response;
    }

    private String formatAddress(String calle, Integer numero, String piso) {
        StringBuilder formattedAddress = new StringBuilder(calle);

        if (numero != null) {
            formattedAddress.append(" ").append(numero);
        }

        if (piso != null) {
            formattedAddress.append(" ").append(piso);
        }

        return formattedAddress.toString();
    }

    public List<PedidoProductoDTO> productosEntregados(Long id) throws ValidacionException, RecordNotFoundException {
        Entrega entrega = entregaRepo.findById(id).orElseThrow(() -> new RecordNotFoundException("No se encontró la entrega"));

        if (!entrega.getEstadoEntrega().getNombreEstadoEntrega().equals("Procesada")) {
            throw new ValidacionException("La entrega aún no ha sido procesada.");
        }

        if (entrega.getEntregaDetalles() == null || entrega.getEntregaDetalles().isEmpty()) {
            return new ArrayList<>();
        }

        return entrega.getEntregaDetalles().stream().map(ed -> PedidoProductoDTO.builder().idProducto(ed.getProducto().getId()).nombreProducto(ed.getProducto().getNombre()).cantidad(ed.getCantidadEntregada()).cantidadRecibida(ed.getCantidadRecibida()).precio(ed.getProducto().getPrecios().stream().filter(pr -> pr.getFechaFinVigencia() == null).findFirst().get().getPrecio()).build()).collect(Collectors.toList());
    }

    public List<PedidoProductoDTO> getProductosDevolver(Long id) throws RecordNotFoundException {
        Entrega entrega = entregaRepo.findById(id).orElseThrow(() -> new RecordNotFoundException("No se encontró la entrega"));
        List<PedidoProductoDTO> productos = new ArrayList<>();

        if (entrega.getDomicilio().getProductoDomicilios() == null || entrega.getDomicilio().getProductoDomicilios().isEmpty()) {
            return productos;
        }

        for (DomicilioProducto dp : entrega.getDomicilio().getProductoDomicilios()) {
            if (dp.getCantidadDevolver() > 0) {
                productos.add(PedidoProductoDTO.builder().idProducto(dp.getProducto().getId()).nombreProducto(dp.getProducto().getNombre()).cantidadDevolver(dp.getCantidadDevolver()).build());
            }
        }

        return productos;
    }

    public EntregaWebDTO detallarEntregaWeb(Long id) throws RecordNotFoundException {
        Entrega entrega = entregaRepo.findById(id).orElseThrow(() -> new RecordNotFoundException("No se encontró la entrega"));

        if (entrega.getEstadoEntrega().getNombreEstadoEntrega().equals("Programada") || entrega.getEstadoEntrega().getNombreEstadoEntrega().equals("Pendiente")) {
            return EntregaWebDTO.builder().nombreRepartidor(entrega.getReparto().getRepartidor() != null ? entrega.getReparto().getRepartidor().getNombre() + " " + entrega.getReparto().getRepartidor().getApellido() : "Sin asignar").repartoId(entrega.getReparto().getId()).fechaEjecucion(entrega.getReparto().getFechaEjecucion()).id(entrega.getId()).ordenVisita(entrega.getOrdenVisita()).observaciones(entrega.getDomicilio().getObservaciones()).nombreCliente(entrega.getDomicilio().getCliente().getNombre() + " " + entrega.getDomicilio().getCliente().getApellido()).domicilio(formatAddress(entrega.getDomicilio().getCalle(), entrega.getDomicilio().getNumero(), entrega.getDomicilio().getPisoDepartamento()) + ", " + entrega.getDomicilio().getLocalidad()).montoRecaudar(entregaRepo.getMontoTotalByEntrega(entrega.getId())).estado(entrega.getEstadoEntrega().getNombreEstadoEntrega()).build();
        }

        if (entrega.getEstadoEntrega().getNombreEstadoEntrega().equals("Cancelada")) {
            return EntregaWebDTO.builder().nombreRepartidor(entrega.getReparto().getRepartidor() != null ? entrega.getReparto().getRepartidor().getNombre() + " " + entrega.getReparto().getRepartidor().getApellido() : "Sin asignar").repartoId(entrega.getReparto().getId()).fechaEjecucion(entrega.getReparto().getFechaEjecucion()).id(entrega.getId()).ordenVisita(entrega.getOrdenVisita()).observacionesEntrega(entrega.getObservaciones()).nombreCliente(entrega.getDomicilio().getCliente().getNombre() + " " + entrega.getDomicilio().getCliente().getApellido()).domicilio(formatAddress(entrega.getDomicilio().getCalle(), entrega.getDomicilio().getNumero(), entrega.getDomicilio().getPisoDepartamento()) + ", " + entrega.getDomicilio().getLocalidad()).montoRecaudar(entregaRepo.getMontoTotalByEntrega(entrega.getId())).estado(entrega.getEstadoEntrega().getNombreEstadoEntrega()).build();
        }

        if (entrega.getEstadoEntrega().getNombreEstadoEntrega().equals("Ausente")) {
            return EntregaWebDTO.builder().nombreRepartidor(entrega.getReparto().getRepartidor().getNombre() + " " + entrega.getReparto().getRepartidor().getApellido()).repartoId(entrega.getReparto().getId()).fechaHoraVisita(entrega.getFechaHoraVisita()).id(entrega.getId()).ordenVisita(entrega.getOrdenVisita()).observacionesEntrega(entrega.getObservaciones()).nombreCliente(entrega.getDomicilio().getCliente().getNombre() + " " + entrega.getDomicilio().getCliente().getApellido()).domicilio(formatAddress(entrega.getDomicilio().getCalle(), entrega.getDomicilio().getNumero(), entrega.getDomicilio().getPisoDepartamento()) + ", " + entrega.getDomicilio().getLocalidad()).montoRecaudar(entregaRepo.getMontoTotalByEntrega(entrega.getId())).estado(entrega.getEstadoEntrega().getNombreEstadoEntrega()).build();
        }

        EntregaWebDTO response = new EntregaWebDTO();
        response.setId(entrega.getId());
        response.setNombreRepartidor(entrega.getReparto().getRepartidor().getNombre() + " " + entrega.getReparto().getRepartidor().getApellido());
        response.setRepartoId(entrega.getReparto().getId());
        response.setFechaHoraVisita(entrega.getFechaHoraVisita());
        response.setEstado(entrega.getEstadoEntrega().getNombreEstadoEntrega());
        response.setDomicilio(formatAddress(entrega.getDomicilio().getCalle(), entrega.getDomicilio().getNumero(), entrega.getDomicilio().getPisoDepartamento()) + ", " + entrega.getDomicilio().getLocalidad());
        response.setNombreCliente(entrega.getDomicilio().getCliente().getNombre() + " " + entrega.getDomicilio().getCliente().getApellido());
        response.setMontoEntregado(entrega.getMonto());
        if (entrega.getPago() != null) {
            response.setMontoRecaudado(entrega.getPago().getTotal());
            response.setMedioPago(entrega.getPago().getMedioPago().getNombre());
        }
        response.setObservacionesEntrega(entrega.getObservaciones());
        return response;
    }
}
