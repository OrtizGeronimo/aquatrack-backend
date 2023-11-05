package com.example.aquatrack_backend.service;

import com.example.aquatrack_backend.config.SecurityUser;
import com.example.aquatrack_backend.dto.EntregaEstadoProjection;
import com.example.aquatrack_backend.dto.ProductoEntregadoProjection;
import com.example.aquatrack_backend.dto.ReporteDTO;
import com.example.aquatrack_backend.model.Empresa;
import com.example.aquatrack_backend.model.Usuario;
import com.example.aquatrack_backend.repo.ClienteRepo;
import com.example.aquatrack_backend.repo.EntregaRepo;
import com.example.aquatrack_backend.repo.PagoRepo;
import com.example.aquatrack_backend.repo.ProductoRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReporteServicio {

    @Autowired
    public EntregaRepo entregaRepo;

    @Autowired
    public PagoRepo pagoRepo;

    @Autowired
    public ClienteRepo clienteRepo;

    @Autowired
    public ProductoRepo productoRepo;

    public ReporteDTO generarReporte(LocalDate fechaDesde, LocalDate fechaHasta) {
        Empresa empresa = getUsuarioFromContext().getPersona().getEmpresa();
        Long countEntregas = entregaRepo.countEntregas(fechaDesde, fechaHasta, empresa.getId());
        BigDecimal totalRecaudado = pagoRepo.getRecaudado(fechaDesde, fechaHasta, empresa.getId());
        Long countClientes = clienteRepo.cantidadClientes(fechaHasta, empresa.getId());
        List<ProductoEntregadoProjection> productosEntregados = productoRepo.getProductosEntregados(fechaDesde, fechaHasta, empresa.getId());
        List<EntregaEstadoProjection> entregaEstado = entregaRepo.getEntregasByEstado(fechaDesde, fechaHasta, empresa.getId());
        return ReporteDTO.builder().cantidadClientes(countClientes).cantidadEntregas(countEntregas).montoRecaudado(totalRecaudado != null ? totalRecaudado : BigDecimal.ZERO).labelProductosEntregados(productosEntregados.stream().map(ProductoEntregadoProjection::getNombre).collect(Collectors.toList())).valueProductosEntregados(productosEntregados.stream().map(ProductoEntregadoProjection::getCantidad).collect(Collectors.toList())).labelEntregas(entregaEstado.stream().map(EntregaEstadoProjection::getEstado).collect(Collectors.toList())).valueEntregas(entregaEstado.stream().map(EntregaEstadoProjection::getCantidad).collect(Collectors.toList())).build();
    }

    private Usuario getUsuarioFromContext() {
        return ((SecurityUser) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal())
                .getUsuario();
    }
}
