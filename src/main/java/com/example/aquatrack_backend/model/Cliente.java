package com.example.aquatrack_backend.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Cliente extends Persona {

  private Integer dni;

  @OneToOne(mappedBy = "cliente", cascade = CascadeType.ALL)
  private Domicilio domicilio;

  @ManyToOne()
  private EstadoCliente estadoCliente;
}
