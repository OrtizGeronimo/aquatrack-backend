package com.example.aquatrack_backend.service;

import com.example.aquatrack_backend.dto.DomicilioDTO;
import com.example.aquatrack_backend.dto.GuardarPedidoDTO;
import com.example.aquatrack_backend.dto.PedidoListDTO;
import com.example.aquatrack_backend.dto.PedidoProductoDTO;
import com.example.aquatrack_backend.exception.RecordNotFoundException;
import com.example.aquatrack_backend.model.Empleado;
import com.example.aquatrack_backend.model.Empresa;
import com.example.aquatrack_backend.model.PedidoProducto;
import com.example.aquatrack_backend.repo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.aquatrack_backend.model.Pedido;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
public class PedidoServicio extends ServicioBaseImpl<Pedido> {

  @Autowired
  private PedidoRepo pedidoRepo;
  @Autowired
  private ProductoRepo productoRepo;
  @Autowired
  private DomicilioRepo domicilioRepo;
  @Autowired
  private TipoPedidoRepo tipoPedidoRepo;
  @Autowired
  private EstadoPedidoRepo estadoPedidoRepo;

  public PedidoServicio(RepoBase<Pedido> repoBase) {
    super(repoBase);
  }

  @Transactional
  public Page<PedidoListDTO> getAllPedidosExtraordinarios(Integer page, Integer size){
    Empresa empresa = ((Empleado) getUsuarioFromContext().getPersona()).getEmpresa();
    Pageable paging = PageRequest.of(page, size);
    Page<Pedido> pedidos = pedidoRepo.getAllPedidosExtraordinarios(empresa.getId(), paging);
    return pedidos.map(this::makePedidoListDTO);
  }

  @Transactional
  public PedidoListDTO createPedido(GuardarPedidoDTO pedido){
    Pedido pedidoNuevo = new Pedido();
    pedidoNuevo.setPedidoProductos(
            pedido.getPedidoProductos().stream().map(
                    pedidoProducto -> new PedidoProducto(pedidoProducto.getCantidad(),
                                                         pedidoNuevo,
                                                         productoRepo.findById(pedidoProducto.getIdProducto()).get()
                                                         )
            ).collect(Collectors.toList())
    );
    pedidoNuevo.setDomicilio(domicilioRepo.findById(pedido.getIdDomicilio()).get());
    pedidoNuevo.setEstadoPedido(estadoPedidoRepo.findById(pedido.getIdEstado()).get());
    pedidoNuevo.setTipoPedido(tipoPedidoRepo.findById(pedido.getIdTipo()).get());
    pedidoNuevo.setFechaCoordinadaEntrega(pedido.getFechaCoordinadaEntrega());
    Pedido p = pedidoRepo.save(pedidoNuevo);
    return makePedidoListDTO(p);
  }

  @Transactional
  public PedidoListDTO detallarPedido(Long idPedido)throws RecordNotFoundException {
    Pedido pedido = pedidoRepo.findById(idPedido).orElseThrow(()-> new RecordNotFoundException("El pedido no fue encontrado"));
    return makePedidoListDTO(pedido);
  }

  private PedidoListDTO makePedidoListDTO(Pedido pedido){
    return PedidoListDTO.builder()
            .id(pedido.getId())
            .pedidoProductos(pedido.getPedidoProductos()
                    .stream().map(pedidoProducto -> PedidoProductoDTO.builder()
                            .cantidad(pedidoProducto.getCantidad())
                            .nombreProducto(pedidoProducto.getProducto().getNombre())
                            .build())
                    .collect(Collectors.toList()))
            .domicilio(DomicilioDTO.builder()
                    .domicilio(pedido.getDomicilio().getCalle() + " " +
                            pedido.getDomicilio().getNumero() + " " +
                            pedido.getDomicilio().getPisoDepartamento())
                    .build())
            .fechaCoordinadaEntrega(pedido.getFechaCoordinadaEntrega())
            .build();
  }
}
