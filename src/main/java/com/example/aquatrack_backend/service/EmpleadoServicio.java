package com.example.aquatrack_backend.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.aquatrack_backend.dto.EmpleadoDTO;
import com.example.aquatrack_backend.dto.TipoEmpleadoDTO;
import com.example.aquatrack_backend.model.Empleado;
import com.example.aquatrack_backend.model.Empresa;
import com.example.aquatrack_backend.repo.EmpleadoRepo;
import com.example.aquatrack_backend.repo.RepoBase;
import com.example.aquatrack_backend.repo.TipoEmpleadoRepo;

@Service
public class EmpleadoServicio extends ServicioBaseImpl<Empleado> {
  @Autowired
  private EmpleadoRepo empleadoRepo;
  @Autowired
  private TipoEmpleadoRepo tipoEmpleadoRepo;

  public EmpleadoServicio(RepoBase<Empleado> repoBase) {
    super(repoBase);
  }

   public Page<EmpleadoDTO> findAllByEnterprise(int page, int size, String nombre, boolean mostrarInactivos) {
      Empresa empresa = ((Empleado) getUsuarioFromContext().getPersona()).getEmpresa();
      Long id = empresa.getId();
      Pageable paging = PageRequest.of(page, size);
      Page<Empleado> empleados = empleadoRepo.findAllByEnterprise(id, nombre, mostrarInactivos, paging);
      Page<EmpleadoDTO> empleadosDTO = empleados.map(empleado -> {
        EmpleadoDTO empleadoDTO = new EmpleadoDTO();
        empleadoDTO.setId((Long) empleado.getId());
        empleadoDTO.setNombre((String) empleado.getNombre());
        empleadoDTO.setApellido((String) empleado.getApellido());
        empleadoDTO.setLegajo((Integer) empleado.getLegajo());
        empleadoDTO.setFechaFinVigencia((LocalDateTime) empleado.getFechaFinVigencia());
        empleadoDTO.setFechaIngreso((LocalDateTime) empleado.getFechaIngreso());
        empleadoDTO.setTipo((String) empleado.getTipo().getNombre());
        return empleadoDTO;
    });
    return empleadosDTO;
  }

  public List<TipoEmpleadoDTO> findAllTiposActive(){
     return tipoEmpleadoRepo.findAllActive()
        .stream()
        .map(tipoEmpleado -> new ModelMapper().map(tipoEmpleado, TipoEmpleadoDTO.class))
        .collect(Collectors.toList());
  }

}
