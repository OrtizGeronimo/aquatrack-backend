package com.example.aquatrack_backend.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.example.aquatrack_backend.config.BingMapsConfig;
import com.example.aquatrack_backend.dto.ListarRepartosDTO;
import com.example.aquatrack_backend.dto.ObjetoGenericoDTO;
import com.example.aquatrack_backend.dto.RepartidorAsignableDTO;
import com.example.aquatrack_backend.dto.RepartoDTO;
import com.example.aquatrack_backend.dto.RepartoParametroDTO;
import com.example.aquatrack_backend.exception.EntidadNoValidaException;
import com.example.aquatrack_backend.exception.RecordNotFoundException;
import com.example.aquatrack_backend.exception.ValidacionException;
import com.example.aquatrack_backend.model.DiaDomicilio;
import com.example.aquatrack_backend.model.DiaRuta;
import com.example.aquatrack_backend.model.Domicilio;
import com.example.aquatrack_backend.model.DomicilioRuta;
import com.example.aquatrack_backend.model.Empleado;
import com.example.aquatrack_backend.model.Empresa;
import com.example.aquatrack_backend.model.Entrega;
import com.example.aquatrack_backend.model.EstadoEntrega;
import com.example.aquatrack_backend.model.EstadoReparto;
import com.example.aquatrack_backend.model.Reparto;
import com.example.aquatrack_backend.model.Ruta;
import com.example.aquatrack_backend.model.Ubicacion;
import com.example.aquatrack_backend.repo.EmpleadoRepo;
import com.example.aquatrack_backend.repo.EmpresaRepo;
import com.example.aquatrack_backend.repo.EstadoEntregaRepo;
import com.example.aquatrack_backend.repo.EstadoRepartoRepo;
import com.example.aquatrack_backend.repo.RepartoRepo;
import com.example.aquatrack_backend.repo.RepoBase;
import com.example.aquatrack_backend.repo.RutaRepo;
import com.google.ortools.Loader;

@Service
public class RepartoServicio extends ServicioBaseImpl<Reparto> {

    @Autowired
    private RepartoRepo repartoRepo;

    @Autowired
    private RutaRepo rutaRepo;

    @Autowired
    private EstadoRepartoRepo estadoRepartoRepo;

    @Autowired
    private EstadoEntregaRepo estadoEntregaRepo;

    @Autowired
    private BingMapsConfig bingMapsConfig;

    @Autowired
    private EmpresaRepo empresaRepo;

    @Autowired
    private EmpleadoRepo empleadoRepo;

    @Autowired
    private RestTemplate restTemplate;

    private ModelMapper mapper = new ModelMapper();


    public RepartoServicio(RepoBase<Reparto> repoBase) {
        super(repoBase);
    }


    static {
        Loader.loadNativeLibraries();
    }

    public ListarRepartosDTO detalleReparto(Long id) throws RecordNotFoundException {
        Empresa empresa = ((Empleado) getUsuarioFromContext().getPersona()).getEmpresa();
        Reparto reparto = repartoRepo.findById(id).orElseThrow(() -> new RecordNotFoundException("El id del reparto ingresado no corresponde a uno existente"));

        ListarRepartosDTO dto = new ListarRepartosDTO();
        dto.setId(reparto.getId());
        dto.setEstado(reparto.getEstadoReparto().getNombre());
        dto.setRepartidor(reparto.getRepartidor() == null ? "Sin Asignar " : reparto.getRepartidor().getNombre() + " " + reparto.getRepartidor().getApellido());
        dto.setFechaEjecucion(reparto.getFechaEjecucion());
        dto.setFechaHoraFin(reparto.getFechaHoraFin());
        dto.setFechaHoraInicio(reparto.getFechaHoraInicio());
        dto.setIdRuta(reparto.getRuta().getId());
        dto.setObservaciones(reparto.getObservaciones());
        dto.setNombreRuta(reparto.getRuta().getNombre());
        dto.setLatitudInicio(empresa.getUbicacion().getLatitud());
        dto.setLongitudInicio(empresa.getUbicacion().getLongitud());
        return dto;
    }

    public RepartoParametroDTO getParametrosReparto(){
        RepartoParametroDTO response = new RepartoParametroDTO();

        Empresa empresa = ((Empleado) getUsuarioFromContext().getPersona()).getEmpresa();

        List<Empleado> empleados = empleadoRepo.findAllByEmpresaIdAndTipoId(empresa.getId(), 2l);

        List<Ruta> rutas = rutaRepo.findAllByEmpresaId(empresa.getId());

        List<EstadoReparto> estadoRepartos = estadoRepartoRepo.findAll();

        response.setRepartidores(empleados.stream().map(empleado -> {
            ObjetoGenericoDTO dto = new ObjetoGenericoDTO();
            dto.setId(empleado.getId());
            dto.setNombre(empleado.getNombre() + " " + empleado.getApellido());
            return dto;
        }).collect(Collectors.toList()));

        response.setRutas(rutas.stream().map(ruta -> {
            ObjetoGenericoDTO dto = new ObjetoGenericoDTO();
            dto.setNombre(ruta.getNombre());
            dto.setId(ruta.getId());
            return dto;
        }).collect(Collectors.toList()));

        response.setEstados(estadoRepartos.stream().map(estado -> {
            ObjetoGenericoDTO dto = new ObjetoGenericoDTO();
            dto.setId(estado.getId());
            dto.setNombre(estado.getNombre());
            return dto;
        }).collect(Collectors.toList()));

        return response;
    }

    @Scheduled(cron = "0 * * * * *")
    @Transactional
    public void generacionAutomaticaRepartos() throws RecordNotFoundException, ValidacionException {

        LocalTime now = LocalTime.now();

        LocalTime today = LocalTime.of(now.getHour(), now.getMinute());

        List<Empresa> empresas = empresaRepo.findAll();

        for (Empresa empresa: empresas) {
            if (empresa.getHoraGeneracionReparto().equals(today)){
                for(Ruta ruta: empresa.getRutas()) {
                    if (ruta.getFechaFinVigencia() == null) {
                        crearReparto(ruta.getId());
                    }
                }
            }
        }
    }

    @Transactional
    public void designarHoraGeneracionAutomaticaReparto(Integer hora, Integer minuto) throws ValidacionException {

        if ((hora < 0 || hora > 23) || (minuto < 0 || minuto > 59)){
            throw new ValidacionException("Debe ingresar un horario válido");
        }

        LocalTime horario = LocalTime.of(hora, minuto);

        Empresa empresa = ((Empleado) getUsuarioFromContext().getPersona()).getEmpresa();

        empresa.setHoraGeneracionReparto(horario);

        empresaRepo.save(empresa);

    }

    @Transactional
    public RepartoDTO crearReparto(Long id) throws RecordNotFoundException, ValidacionException {

        Ruta ruta = rutaRepo.findById(id).orElseThrow(() -> new RecordNotFoundException("La ruta no fue encontrada"));

        LocalDateTime now = LocalDateTime.now();

        DayOfWeek dayOfWeek = now.getDayOfWeek();

        int idDia = dayOfWeek.getValue();

        List<Entrega> entregasARepartir = new ArrayList<>();
        for(DiaRuta dia : ruta.getDiaRutas()){
          if(dia.getDiaSemana().getId() == idDia){
            for (DiaDomicilio diaDomicilio : dia.getDiaDomicilios()) {
              if(diaDomicilio.getDomicilio().getFechaFinVigencia() == null){
                Entrega entrega = new Entrega();
                Domicilio domicilio = diaDomicilio.getDomicilio();
                entrega.setDomicilio(domicilio);
                entregasARepartir.add(entrega);
              }
            }
            break;
          }
        }
        
        EstadoReparto estadoAnticipado = estadoRepartoRepo.findByNombre("Anticipado");

        Reparto reparto = new Reparto();

        List<Entrega> entregasExistentes = new ArrayList<>();

        for (Reparto repartoExistente: ruta.getRepartos()) {
            if (repartoExistente.getEstadoReparto().equals(estadoAnticipado) && repartoExistente.getFechaEjecucion().equals(now)){
                reparto = repartoExistente;
                entregasExistentes = reparto.getEntregas();
            }
        }

        entregasARepartir.addAll(entregasExistentes);
        if (entregasARepartir.isEmpty()){
            throw new ValidacionException("La ruta no contiene entregas a realizar en el dia");
        }
        List<Entrega> rutaOptima = calcularRutaOptima(entregasARepartir, ruta);
        EstadoReparto estado = estadoRepartoRepo.findByNombre("Pendiente de Asignación");
        EstadoEntrega estadoEntrega = estadoEntregaRepo.findByNombreEstadoEntrega("Programada");

        reparto.setRuta(ruta);
        reparto.setEstadoReparto(estado);
        reparto.setFechaEjecucion(now);

        List<Entrega> entregas = new ArrayList<>();

        for (int i = 0; i < rutaOptima.size(); i++) {
                Entrega entrega = rutaOptima.get(i);
                entrega.setEstadoEntrega(estadoEntrega);
                entrega.setReparto(reparto);
                entrega.setOrdenVisita(i);
                entregas.add(entrega);
        }

        reparto.setEntregas(entregas);

        repartoRepo.save(reparto);

        return mapper.map(reparto, RepartoDTO.class);

    }

    private List<Entrega> calcularRutaOptima(List<Entrega> domicilioRutas, Ruta ruta) throws ValidacionException {
        String apiKey = bingMapsConfig.getApiKey();
        try {
        StringBuilder urlBuilder = new StringBuilder("https://dev.virtualearth.net/REST/v1/Routes/Driving?");

        Ubicacion ubiEmpresa = ruta.getEmpresa().getUbicacion();

        double latInicio = ubiEmpresa.getLatitud();
        double lonInicio = ubiEmpresa.getLongitud();
        String latEmpresa = Double.toString(latInicio).replace(',', '.');
        String lonEmpresa = Double.toString(lonInicio).replace(',', '.');

        String coordenadasInicio = latEmpresa + "," + lonEmpresa;
        urlBuilder.append("wp.1=" + coordenadasInicio + "&");
        for (int i = 0; i < domicilioRutas.size(); i++) {
            Domicilio domicilio1 = domicilioRutas.get(i).getDomicilio();
            double lat1 = domicilio1.getUbicacion().getLatitud();
            double lon1 = domicilio1.getUbicacion().getLongitud();
            String originLatitude = Double.toString(lat1).replace(',', '.');
            String originLongitude = Double.toString(lon1).replace(',', '.');

            String coordinates = originLatitude + "," + originLongitude;
            urlBuilder.append("wp." + (i + 2) + "=" + coordinates + "&");
        }
        urlBuilder.append("wp." + (domicilioRutas.size() + 2) + "=" + coordenadasInicio + "&");


        urlBuilder.append("optimizeWaypoints=true&optmz=distance&key=" + apiKey);

        String url = urlBuilder.toString();

        // Create an HTTP connection and send the request
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("GET");

        // Read the response
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String line;
        StringBuilder response = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();


        List<Entrega> ubicacionesOrdenadas = new ArrayList<>();

        JSONObject jsonResponse = new JSONObject(response.toString());
        JSONArray coordinates = jsonResponse.getJSONArray("resourceSets")
                    .getJSONObject(0)
                    .getJSONArray("resources")
                    .getJSONObject(0)
                    .getJSONArray("waypointsOrder");

            for (int i = 1; i <= coordinates.length() - 2; i++) {
                String coordenada = coordinates.getString(i);
                String[] partes = coordenada.split("\\.");

                String numeroString = partes[1];

                Integer indiceCoordenada = Integer.parseInt(numeroString) - 1;

                ubicacionesOrdenadas.add(domicilioRutas.get(indiceCoordenada));
            }
//            ubicacionesOrdenadas.add(domicilioRutas.get(0));


        connection.disconnect();
            return ubicacionesOrdenadas;
        } catch (Exception e){
            System.out.println(e.getMessage());
            e.printStackTrace();
            throw new ValidacionException("Hubo un error al optimizar la ruta");
        }

    }


    public Page<ListarRepartosDTO> listarRepartos(Long estado, Long idRepartidor, Long idRuta, int page, int size) throws RecordNotFoundException {

        Pageable pageable = PageRequest.of(page, size/*, Sort.by("er.id, ru.nombre")*/);

        Empresa empresa = ((Empleado) getUsuarioFromContext().getPersona()).getEmpresa();

        Page<Reparto> repartos = repartoRepo.search(empresa.getId(), idRuta, idRepartidor, estado, pageable);

//        if (repartos == null || repartos.isEmpty()){
//            return null;
//        }

        Page<ListarRepartosDTO> response = repartos.map(reparto -> {
            ListarRepartosDTO dto = new ListarRepartosDTO();
            dto.setId(reparto.getId());
            dto.setEstado(reparto.getEstadoReparto().getNombre());
            dto.setCantEntregas(reparto.getEntregas().size());
            dto.setRepartidor(reparto.getRepartidor() == null ? "Sin Asignar " : reparto.getRepartidor().getNombre() + " " + reparto.getRepartidor().getApellido());
            dto.setFechaEjecucion(reparto.getFechaEjecucion());
            dto.setFechaHoraFin(reparto.getFechaHoraFin());
            dto.setIdRuta(reparto.getRuta().getId());
            dto.setNombreRuta(reparto.getRuta().getNombre());
            return dto;
        });

        return response;

    }


    @Transactional
    public RepartoDTO crearRepartoManual(Long id) throws RecordNotFoundException, ValidacionException {
        Ruta ruta = rutaRepo.findById(id).orElseThrow(() -> new RecordNotFoundException("La ruta no fue encontrada"));

        EstadoReparto pendienteAsignacion = estadoRepartoRepo.findByNombre("Pendiente de Asignación");
        EstadoReparto pendienteEjecucion = estadoRepartoRepo.findByNombre("Pendiente de Ejecución");
        EstadoReparto enEjecucion = estadoRepartoRepo.findByNombre("En Ejecución");

        if (ruta.getRepartos() != null && !ruta.getRepartos().isEmpty()){
            LocalDateTime now = LocalDateTime.now();
            for (Reparto repartoExistente: ruta.getRepartos()) {
                if (repartoExistente.getFechaEjecucion().equals(now)){
                    if (repartoExistente.getEstadoReparto().equals(pendienteAsignacion) || repartoExistente.getEstadoReparto().equals(pendienteEjecucion) || repartoExistente.getEstadoReparto().equals(enEjecucion)){
                        throw new ValidacionException("Ya existe un reparto para la ruta indicada que aún no ha finalizado");
                    }
                }
            }
        }
        return crearReparto(ruta.getId());
    }
  
    @Transactional
    public List<RepartidorAsignableDTO> getAsignarRepartidor() {
        Empresa empresa = ((Empleado) getUsuarioFromContext().getPersona()).getEmpresa();

        List<Empleado> repartidores = empleadoRepo.findRepartidoresLibres(empresa.getId());

        return repartidores.stream()
                           .map( empleado -> RepartidorAsignableDTO.builder()
                                                                   .id(empleado.getId())
                                                                   .nombreRepartidor(empleado.getNombre() + " " + empleado.getApellido())
                                                                   .build())
                           .collect(Collectors.toList());
                                                                  
    }

    @Transactional
    public ListarRepartosDTO asignarRepartidor(Long idReparto, Long idRepartidor) throws RecordNotFoundException, ValidacionException {
        Reparto reparto = repartoRepo.findById(idReparto).orElseThrow(() -> new RecordNotFoundException("El id del reparto ingresado no corresponde a uno existente"));
        Empleado empleado = empleadoRepo.findById(idRepartidor).orElseThrow(() -> new RecordNotFoundException("El id del repartidor ingresado no corresponde a uno existente"));


        EstadoReparto pendienteEjecucion = estadoRepartoRepo.findByNombre("Pendiente de Ejecución");
        EstadoReparto pendienteAsignacion = estadoRepartoRepo.findByNombre("Pendiente de Asignación");

        if (!reparto.getEstadoReparto().equals(pendienteAsignacion) && !reparto.getEstadoReparto().equals(pendienteEjecucion)){
            throw new ValidacionException("No se puede asignar un repartidor");
        }

        reparto.setRepartidor(empleado);
        reparto.setEstadoReparto(pendienteEjecucion);

        repartoRepo.save(reparto);

        ListarRepartosDTO dto = new ListarRepartosDTO();
        dto.setId(reparto.getId());
        dto.setEstado(reparto.getEstadoReparto().getNombre());
        dto.setRepartidor(reparto.getRepartidor() == null ? "Sin Asignar " : reparto.getRepartidor().getNombre() + " " + reparto.getRepartidor().getApellido());
        dto.setFechaEjecucion(reparto.getFechaEjecucion());
        dto.setFechaHoraFin(reparto.getFechaHoraFin());
        dto.setFechaHoraInicio(reparto.getFechaHoraInicio());
        dto.setIdRuta(reparto.getRuta().getId());
        dto.setObservaciones(reparto.getObservaciones());
        dto.setNombreRuta(reparto.getRuta().getNombre());
        dto.setLatitudInicio(empleado.getEmpresa().getUbicacion().getLatitud());
        dto.setLongitudInicio(empleado.getEmpresa().getUbicacion().getLongitud());
        return dto;
    }

    @Transactional
    public void iniciarReparto(Long idReparto) throws RecordNotFoundException {
        Reparto reparto = repartoRepo.findById(idReparto).orElseThrow(() -> new RecordNotFoundException("El id del reparto ingresado no corresponde a uno existente"));

        EstadoReparto enEjecucion = estadoRepartoRepo.findByNombre("En Ejecución");

        reparto.setEstadoReparto(enEjecucion);
        reparto.setFechaHoraInicio(LocalDateTime.now());

        EstadoEntrega pendiente = estadoEntregaRepo.findByNombreEstadoEntrega("Pendiente");

        for (Entrega entrega: reparto.getEntregas()) {
            entrega.setEstadoEntrega(pendiente);
        }

//        notificacionesServicio.enviarInicioReparto()

        repartoRepo.save(reparto);

    }

    @Transactional
    public ListarRepartosDTO cancelarReparto(Long idReparto) throws ValidacionException, RecordNotFoundException {
        Reparto reparto = repartoRepo.findById(idReparto).orElseThrow(() -> new RecordNotFoundException("El ID del reparto ingresado no corresponde a uno existente"));
        Empresa empresa = ((Empleado) getUsuarioFromContext().getPersona()).getEmpresa();
        EstadoReparto enEjecucion = estadoRepartoRepo.findByNombre("En Ejecución");
        EstadoReparto cancelado = estadoRepartoRepo.findByNombre("Cancelado");
        EstadoReparto finalizado = estadoRepartoRepo.findByNombre("Finalizado");

        if (reparto.getEstadoReparto().equals(enEjecucion)){
            throw new ValidacionException("No se puede cancelar un reparto que se encuentra En Ejecución");
        }
        if (reparto.getEstadoReparto().equals(cancelado)){
            throw new ValidacionException("El reparto ya está cancelado");
        }
        if (reparto.getEstadoReparto().equals(finalizado)){
            throw new ValidacionException("No se puede cancelar un reparto Finalizado");
        }

        reparto.setEstadoReparto(cancelado);
        EstadoEntrega cancelada = estadoEntregaRepo.findByNombreEstadoEntrega("Cancelada");
        for (Entrega entrega : reparto.getEntregas()) {
          entrega.setEstadoEntrega(cancelada);
        }
        repartoRepo.save(reparto);
        ListarRepartosDTO dto = new ListarRepartosDTO();
        dto.setId(reparto.getId());
        dto.setEstado(reparto.getEstadoReparto().getNombre());
        dto.setRepartidor(reparto.getRepartidor() == null ? "Sin Asignar " : reparto.getRepartidor().getNombre() + " " + reparto.getRepartidor().getApellido());
        dto.setFechaEjecucion(reparto.getFechaEjecucion());
        dto.setFechaHoraFin(reparto.getFechaHoraFin());
        dto.setFechaHoraInicio(reparto.getFechaHoraInicio());
        dto.setIdRuta(reparto.getRuta().getId());
        dto.setNombreRuta(reparto.getRuta().getNombre());
        dto.setObservaciones(reparto.getObservaciones());
        dto.setLatitudInicio(empresa.getUbicacion().getLatitud());
        dto.setLongitudInicio(empresa.getUbicacion().getLongitud());
        return dto;
    }

    public Map<String, Integer> checkEntregasIncompletas(long idReparto) throws RecordNotFoundException, ValidacionException {
      Reparto reparto = repartoRepo.findById(idReparto).orElseThrow(() -> new RecordNotFoundException("El id del reparto ingresado no corresponde a uno existente"));
      EstadoReparto enEjecucion = estadoRepartoRepo.findByNombre("En Ejecución");

      if (!reparto.getEstadoReparto().equals(enEjecucion)){
        throw new ValidacionException("No se puede finalizar un reparto que no se encuentra en ejecución");
      }

      EstadoEntrega pendiente = estadoEntregaRepo.findByNombreEstadoEntrega("Pendiente");
      Integer cantEntregasPendientes = 0;
      for (Entrega entrega : reparto.getEntregas()) {
        if(entrega.getEstadoEntrega().equals(pendiente)){
          cantEntregasPendientes++;
        }
      }
      HashMap<String, Integer> response = new HashMap<>();
      response.put("cant_entregas_pendientes", cantEntregasPendientes);
      return response;
    }

    @Transactional
    public ListarRepartosDTO finalizarReparto(Long idReparto, String observaciones) throws EntidadNoValidaException, RecordNotFoundException {
        Reparto reparto = repartoRepo.findById(idReparto).orElseThrow(() -> new RecordNotFoundException("El id del reparto ingresado no corresponde a uno existente"));
        HashMap<String, String> errors = new HashMap<>();

        if (observaciones == null || observaciones.isEmpty()){
          errors.put("observaciones", "No puede tener observaciones en blanco.");
        }

        EstadoReparto enEjecucion = estadoRepartoRepo.findByNombre("En Ejecución");

        if (!reparto.getEstadoReparto().equals(enEjecucion)){
          errors.put("root", "No se puede finalizar un reparto que no se encuentra en ejecución");
        }

        if(!errors.isEmpty()){
          throw new EntidadNoValidaException(errors);
        }

        reparto.setObservaciones(observaciones);
        reparto.setFechaHoraFin(LocalDateTime.now());
        
        EstadoEntrega pendiente = estadoEntregaRepo.findByNombreEstadoEntrega("Pendiente");
        EstadoEntrega cancelada = estadoEntregaRepo.findByNombreEstadoEntrega("Cancelada");
        Integer cantEntregasPendientes = 0;
        for (Entrega entrega : reparto.getEntregas()) {
          if(entrega.getEstadoEntrega().equals(pendiente)){
            entrega.setEstadoEntrega(cancelada);
            cantEntregasPendientes++;
          }
        }

        if(cantEntregasPendientes > 0){
          reparto.setEstadoReparto(estadoRepartoRepo.findByNombre("Incompleto"));
        }else{
          reparto.setEstadoReparto(estadoRepartoRepo.findByNombre("Finalizado"));
        } 
        repartoRepo.save(reparto);

        Empresa empresa = ((Empleado) getUsuarioFromContext().getPersona()).getEmpresa();
        ListarRepartosDTO dto = new ListarRepartosDTO();
        dto.setId(reparto.getId());
        dto.setEstado(reparto.getEstadoReparto().getNombre());
        dto.setRepartidor(reparto.getRepartidor() == null ? "Sin Asignar " : reparto.getRepartidor().getNombre() + " " + reparto.getRepartidor().getApellido());
        dto.setFechaEjecucion(reparto.getFechaEjecucion());
        dto.setFechaHoraFin(reparto.getFechaHoraFin());
        dto.setFechaHoraInicio(reparto.getFechaHoraInicio());
        dto.setIdRuta(reparto.getRuta().getId());
        dto.setNombreRuta(reparto.getRuta().getNombre());
        dto.setLatitudInicio(empresa.getUbicacion().getLatitud());
        dto.setObservaciones(reparto.getObservaciones());
        dto.setLongitudInicio(empresa.getUbicacion().getLongitud());
        return dto;
    }
}

