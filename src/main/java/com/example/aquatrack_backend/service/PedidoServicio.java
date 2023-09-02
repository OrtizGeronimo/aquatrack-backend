package com.example.aquatrack_backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.aquatrack_backend.model.Pedido;
import com.example.aquatrack_backend.repo.EstadoPedidoRepo;
import com.example.aquatrack_backend.repo.PedidoExtraordinarioRepo;
import com.example.aquatrack_backend.repo.PedidoRepo;
import com.example.aquatrack_backend.repo.RepoBase;
import com.example.aquatrack_backend.repo.TipoPedidoRepo;

@Service
public class PedidoServicio extends ServicioBaseImpl<Pedido> {

  @Autowired
  private PedidoRepo pedidoHabitualRepo;
  @Autowired
  private PedidoExtraordinarioRepo pedidoRepo;
  @Autowired
  private TipoPedidoRepo tipoPedidoRepo;
  @Autowired
  private EstadoPedidoRepo estadoPedidoRepo;

  public PedidoServicio(RepoBase<Pedido> repoBase) {
    super(repoBase);
  }
}
