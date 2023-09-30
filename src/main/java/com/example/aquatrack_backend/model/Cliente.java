package com.example.aquatrack_backend.model;

import java.util.List;

import javax.persistence.*;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Cascade;

@Getter
@Setter
@NoArgsConstructor
@Entity
@TableGenerator(name = "client_gen", table = "sequence_generator", pkColumnName = "sequence_name", valueColumnName = "next_val", allocationSize = 1)
public class Cliente extends Persona {

  private Integer dni;

  @OneToOne(mappedBy = "cliente")
  @Cascade(org.hibernate.annotations.CascadeType.ALL)
  private Domicilio domicilio;

  @ManyToOne()
  private Empresa empresa;
}
