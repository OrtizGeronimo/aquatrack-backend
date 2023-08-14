package com.example.aquatrack_backend.service;

import com.example.aquatrack_backend.model.EstadoPedido;
import com.example.aquatrack_backend.repo.PedidoRepo;
import com.example.aquatrack_backend.repo.PedidoExtraordinarioRepo;
import com.example.aquatrack_backend.repo.TipoPedidoRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PedidoServicioImpl implements ServicioBase {

    @Autowired
    private PedidoRepo pedidoHabitualRepo;
    private PedidoExtraordinarioRepo pedidoRepo;
    private TipoPedidoRepo tipoPedidoRepo;
    private EstadoPedido estadoPedidoRepo;
}
