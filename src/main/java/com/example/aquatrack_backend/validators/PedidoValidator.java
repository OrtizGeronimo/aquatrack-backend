package com.example.aquatrack_backend.validators;

import com.example.aquatrack_backend.dto.GuardarPedidoDTO;
import com.example.aquatrack_backend.dto.PedidoProductoDTO;
import com.example.aquatrack_backend.exception.PedidoNoValidoException;
import com.example.aquatrack_backend.model.DiaDomicilio;
import com.example.aquatrack_backend.model.Domicilio;
import com.example.aquatrack_backend.model.Pedido;
import com.example.aquatrack_backend.repo.DomicilioRepo;
import com.example.aquatrack_backend.repo.PedidoRepo;
import com.example.aquatrack_backend.repo.ProductoRepo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Service
public class PedidoValidator {

    @Autowired
    PedidoRepo pedidoRepo;

    @Autowired
    DomicilioRepo domicilioRepo;

    @Autowired
    ProductoRepo productoRepo;

    public void validateCreacionPedido(GuardarPedidoDTO pedido) throws PedidoNoValidoException {

        Domicilio domicilio = domicilioRepo.findById(pedido.getIdDomicilio()).get();

        HashMap<String, String> errors = new HashMap<>();

        if(!validateProductosVigentes(pedido)){
            errors.put("root", "El pedido posee productos que no se encuentran vigentes.");
        }

        if(!validateCantidadesProducto(pedido)){
            errors.put("root", "El pedido excede la cantidad máxima en alguno de los productos.");
        }

        if(!validateFechaCorrecta(pedido.getFechaCoordinadaEntrega(), domicilio)){
            errors.put("fechaCoordinadaEntrega", "La fecha del pedido no coincide con una visita planificada al mismo.");
        }

        if(!validatePedidoUnico(pedido.getFechaCoordinadaEntrega(),  domicilio)){
            errors.put("fechaCoordinadaEntrega", "El domicilio ya posee un pedido definido para la fecha ingresada.");
        }

        if(!errors.isEmpty()){
            throw new PedidoNoValidoException(errors);
        }
    }

    private boolean validateProductosVigentes(GuardarPedidoDTO pedido){

        boolean checked = true;

        for(PedidoProductoDTO pedidoProducto: pedido.getPedidoProductos()){
            LocalDateTime fecha = productoRepo.findById(pedidoProducto.getIdProducto()).get().getFechaFinVigencia();
            if(fecha != null){
                checked = false;
                break;
            }
        }

        return checked;
    }

    private boolean validateCantidadesProducto(GuardarPedidoDTO pedido){

        boolean checked = true;

        for (PedidoProductoDTO pedidoProducto: pedido.getPedidoProductos()) {
            Integer cantidadProducto = productoRepo.findById(pedidoProducto.getIdProducto()).get().getMaximo();
            if(pedidoProducto.getCantidad() > cantidadProducto){
                checked = false;
                break;
            }
        }

        return checked;
    }

    private boolean validateFechaCorrecta(LocalDate fechaPedido, Domicilio domicilio){

        DayOfWeek dia = fechaPedido.getDayOfWeek();

        List<DiaDomicilio> diaDomicilios = domicilio.getDiaDomicilios();

        boolean checked = false;

        for (DiaDomicilio diaDomicilio: diaDomicilios) {
            if(diaDomicilio.getDiaRuta().getDiaSemana().getId() == dia.getValue()){
                checked = true;
                break;
            }
        }

        return checked;
    }

    private boolean validatePedidoUnico(LocalDate fechaPedido, Domicilio domicilio) {

        List<Pedido> pedidos = domicilio.getPedidos().stream()
                .filter(
                        pedido -> pedido.getTipoPedido().getId() == 2L &&
                                (pedido.getEstadoPedido().getId() == 1L || pedido.getEstadoPedido().getId() == 2L)
                )
                .collect(Collectors.toList());

        boolean checked = true;

        for (Pedido p: pedidos) {
            if(p.getFechaCoordinadaEntrega().equals(fechaPedido)){
                checked = false;
                break;
            }
        }

        return checked;
    }
}
