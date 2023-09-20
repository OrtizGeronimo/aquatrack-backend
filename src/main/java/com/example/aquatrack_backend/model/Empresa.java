package com.example.aquatrack_backend.model;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Empresa {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String nombre;
  private String direccion;
  private String numTelefono;
  private String email;
  private String url;

  @DateTimeFormat(pattern = "dd-MM-YYYY")
  private LocalDateTime fechaCreacion;
  @DateTimeFormat(pattern = "dd-MM-YYYY")
  private LocalDateTime fechaFinVigencia;
  @DateTimeFormat(pattern = "dd-MM-YYYY' 'HH:mm:ss")
  private LocalDateTime horaActualizacion;

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "empresa")
  private List<CodigoTemporal> codigos;

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "empresa")
  private List<Empleado> empleados;


  @OneToOne(mappedBy = "empresa")
  private Cobertura cobertura;

  @OneToOne()
  private Ubicacion ubicacion;

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "empresa", fetch = FetchType.EAGER)
  private List<Rol> roles;

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "empresa")
  private List<Producto> productos;

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "empresa")
  private List<Cliente> clientes;
}