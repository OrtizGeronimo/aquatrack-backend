package com.example.aquatrack_backend.service;

import com.example.aquatrack_backend.dto.*;
import com.example.aquatrack_backend.exception.RecordNotFoundException;
import com.example.aquatrack_backend.model.Entrega;
import com.example.aquatrack_backend.model.Reparto;
import com.example.aquatrack_backend.repo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
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

    public EntregaServicio(RepoBase<Entrega> repoBase) {
        super(repoBase);
    }

    @Transactional
    public List<EntregaListDTO> findAllEntregasByReparto(Long idReparto) throws RecordNotFoundException {
        Reparto reparto = repartoRepo.findById(idReparto).orElseThrow(() -> new RecordNotFoundException("No se encontró el reparto"));
        return entregaRepo.findAllByReparto(reparto.getId()).stream().map(entrega -> EntregaListDTO.builder().id(entrega.getId()).fechaHoraVisita(entrega.getFechaHoraVisita()).estadoEntregaId(entrega.getEstadoEntrega().getId()).ordenVisita(entrega.getOrdenVisita()).latitudDomicilio(entrega.getDomicilio().getUbicacion().getLatitud()).longitudDomicilio(entrega.getDomicilio().getUbicacion().getLongitud()).estadoEntrega(entrega.getEstadoEntrega().getNombreEstadoEntrega()).cliente(entrega.getDomicilio().getCliente().getNombre() + " " + entrega.getDomicilio().getCliente().getApellido()).build()).collect(Collectors.toList());
    }

    @Transactional
    public EntregaDTO detallarEntrega(Long idEntrega) throws RecordNotFoundException {
        Entrega entrega = entregaRepo.findById(idEntrega).orElseThrow(() -> new RecordNotFoundException("No se encontró la entrega"));
        return EntregaDTO.builder().id(entrega.getId()).estadoEntrega(EstadoEntregaDTO.builder().nombreEstadoEntrega(entrega.getEstadoEntrega().getNombreEstadoEntrega()).build()).domicilio(DomicilioDTO.builder().calle(entrega.getDomicilio().getCalle()).numero(entrega.getDomicilio().getNumero()).pisoDepartamento(entrega.getDomicilio().getPisoDepartamento()).observaciones(entrega.getDomicilio().getObservaciones()).build()).ordenVisita(entrega.getOrdenVisita()).build();
    }

    @Transactional
    public EntregaListDTO disableEntrega(Long idEntrega) throws RecordNotFoundException {
        Entrega entrega = entregaRepo.findById(idEntrega).orElseThrow(() -> new RecordNotFoundException("No se encontró la entrega"));
        entrega.setEstadoEntrega(estadoEntregaRepo.findByNombreEstadoEntrega("Cancelada"));
        entregaRepo.save(entrega);
        return EntregaListDTO.builder().id(entrega.getId()).build();
    }

    public boolean procesarEntrega(Long idEntrega) {
        return true;
    }

    public List<PedidoProductoDTO> productosEntregar(Long idEntrega) throws RecordNotFoundException {
        Entrega entrega = entregaRepo.findById(idEntrega).orElseThrow(() -> new RecordNotFoundException("No se encontró la entrega"));


        if (entrega.getDomicilio().getProductoDomicilios().isEmpty()) {
            List<PedidoProductoProjection> allProducts = pedidoProductoRepo.getAllPedidoProductos(entrega.getId());
            return allProducts.stream().map(pp -> PedidoProductoDTO.builder().idProducto(pp.getId()).nombreProducto(pp.getNombre()).cantidad(pp.getCantidad()).precio(pp.getPrecio()).build()).collect(Collectors.toList());
        }

        return new ArrayList<>();
    }
}
