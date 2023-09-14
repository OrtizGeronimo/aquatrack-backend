package com.example.aquatrack_backend.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.TableGenerator;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@TableGenerator(name = "client_gen", table = "sequence_generator", pkColumnName = "sequence_name", valueColumnName = "next_val", allocationSize = 1)
public class Cliente extends Persona {

  private Integer dni;

  @OneToOne(mappedBy = "cliente")
  private Domicilio domicilio;

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "cliente")
  private List<EmpresaCliente> empresaClientes;
}
