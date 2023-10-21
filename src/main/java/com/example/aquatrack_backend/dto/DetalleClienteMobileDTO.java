package com.example.aquatrack_backend.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DetalleClienteMobileDTO {
  private String nombre;
  private String apellido;
  private String numTelefono;
  private int dni;
  private String empresa;
  private String direccionEmail;
  private String calle;
  private String numero;
  private String pisoDepto;
  private String observaciones;
  private String localidad;
  private Double latitud;
  private Double longitud;
}
