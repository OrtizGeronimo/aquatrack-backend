package com.example.aquatrack_backend.service;

import com.example.aquatrack_backend.config.BingMapsConfig;
import com.example.aquatrack_backend.dto.*;
import com.example.aquatrack_backend.email.EmailService;
import com.example.aquatrack_backend.exception.EntidadNoValidaException;
import com.example.aquatrack_backend.exception.RecordNotFoundException;
import com.example.aquatrack_backend.exception.UserUnauthorizedException;
import com.example.aquatrack_backend.exception.ValidacionException;
import com.example.aquatrack_backend.model.*;
import com.example.aquatrack_backend.repo.*;
import com.google.ortools.Loader;
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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RepartoServicio extends ServicioBaseImpl<Reparto> {

    @Autowired
    private RepartoRepo repartoRepo;

    @Autowired
    private DomicilioServicio domicilioServicio;

    @Autowired
    private RutaRepo rutaRepo;

    @Autowired
    private DomicilioRepo domicilioRepo;


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
    private EntregaRepo entregaRepo;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    public EmailService emailService;

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
    
    public RepartoParametroDTO getParametrosReparto() {
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

        for (Empresa empresa : empresas) {
            if (empresa.getHoraGeneracionReparto().equals(today)) {
                for (Ruta ruta : empresa.getRutas()) {
                    if (ruta.getFechaFinVigencia() == null) {
                        crearReparto(ruta.getId());
                    }
                }
            }
        }
    }

    @Transactional
    public void designarHoraGeneracionAutomaticaReparto(Integer hora, Integer minuto) throws ValidacionException {

        if ((hora < 0 || hora > 23) || (minuto < 0 || minuto > 59)) {
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
        LocalDate localDate = LocalDate.now();

        DayOfWeek dayOfWeek = now.getDayOfWeek();

        int idDia = dayOfWeek.getValue();

        List<Entrega> entregasARepartir = new ArrayList<>();
        for (DiaRuta dia : ruta.getDiaRutas()) {
            if (dia.getDiaSemana().getId() == idDia) {
                for (DiaDomicilio diaDomicilio : dia.getDiaDomicilios()) {
                    if (diaDomicilio.getDomicilio().getFechaFinVigencia() == null) {
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

        for (Reparto repartoExistente : ruta.getRepartos()) {
            if (repartoExistente.getEstadoReparto().equals(estadoAnticipado) && repartoExistente.getFechaEjecucion().equals(now)) {
                reparto = repartoExistente;
                entregasExistentes = reparto.getEntregas();
            }
        }

        entregasARepartir.addAll(entregasExistentes);
        if (entregasARepartir.isEmpty()) {
            return null;
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

            EntregaPedido entregaPedido = new EntregaPedido();
            entregaPedido.setEntrega(entrega);
            entregaPedido.setPedido(domicilioServicio.getPedidoHabitual(entrega.getDomicilio()));
            entrega.setEntregaPedidos(new ArrayList<>());
            entrega.getEntregaPedidos().add(entregaPedido);

            for (Pedido pedido : entrega.getDomicilio().getPedidos().stream().filter(pedido -> pedido.getTipoPedido().getId() != 1 && pedido.getFechaCoordinadaEntrega().equals(localDate)).collect(Collectors.toList())) {
                entregaPedido = new EntregaPedido();
                entregaPedido.setPedido(pedido);
                entregaPedido.setEntrega(entrega);
                entrega.getEntregaPedidos().add(entregaPedido);
            }

            entregas.add(entrega);

        }

        reparto.setEntregas(entregas);

        repartoRepo.save(reparto);

        return mapper.map(reparto, RepartoDTO.class);

    }

/*    @Transactional
    public boolean crearRepartoAnticipado(Long idRuta, LocalDateTime fechaPedido, Domicilio domicilio) throws RecordNotFoundException{

        Reparto reparto = new Reparto();
        Entrega entrega = new Entrega();

        Ruta ruta = rutaRepo.findById(idRuta).orElseThrow(() -> new RecordNotFoundException("La ruta no fue encontrada"));
        List<Reparto> repartosAnticipados = ruta.getRepartos().stream()
                .filter(rep -> rep.getEstadoReparto().getNombre().equalsIgnoreCase("Anticipado"))
                .collect(Collectors.toList());

        List<Entrega> entregas = new ArrayList<>();
        for (Reparto r: repartosAnticipados) {
            if (r.getFechaEjecucion().equals(fechaPedido)) {
                reparto = r;
                entregas = r.getEntregas();
                break;
            }
        }

        entrega.setDomicilio(domicilio);
        entrega.setReparto(reparto);
        entrega.setEstadoEntrega(estadoEntregaRepo.findByNombreEstadoEntrega("Programada"));
        entregas.add(entrega);
        reparto.setEntregas(entregas);

        reparto.setFechaEjecucion(fechaPedido);
        reparto.setEstadoReparto(estadoRepartoRepo.findByNombre("Anticipado"));

        if(reparto.getRuta() == null){
            reparto.setRuta(ruta);
        }

        repartoRepo.save(reparto);
        return true;
    }*/

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
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            throw new ValidacionException("Hubo un error al optimizar la ruta");
        }

    }


    public Page<ListarRepartosDTO> listarRepartos(Long estado, Long idRepartidor, Long idRuta, LocalDate fechaEjecucionDesde, LocalDate fechaEjecucionHasta, int page, int size) throws RecordNotFoundException {

        Pageable pageable = PageRequest.of(page, size/*, Sort.by("er.id, ru.nombre")*/);

        Empresa empresa = ((Empleado) getUsuarioFromContext().getPersona()).getEmpresa();

        Page<Reparto> repartos = repartoRepo.search(empresa.getId(), idRuta, idRepartidor, estado, fechaEjecucionDesde, fechaEjecucionHasta, pageable);

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

        if (ruta.getRepartos() != null && !ruta.getRepartos().isEmpty()) {
            LocalDateTime now = LocalDateTime.now();
            for (Reparto repartoExistente : ruta.getRepartos()) {
                if (repartoExistente.getFechaEjecucion().equals(now)) {
                    if (repartoExistente.getEstadoReparto().equals(pendienteAsignacion) || repartoExistente.getEstadoReparto().equals(pendienteEjecucion) || repartoExistente.getEstadoReparto().equals(enEjecucion)) {
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

        return repartidores.stream().map(empleado -> RepartidorAsignableDTO.builder().id(empleado.getId()).nombreRepartidor(empleado.getNombre() + " " + empleado.getApellido()).build()).collect(Collectors.toList());

    }

    @Transactional
    public ListarRepartosDTO asignarRepartidor(Long idReparto, Long idRepartidor) throws RecordNotFoundException, ValidacionException {
        Reparto reparto = repartoRepo.findById(idReparto).orElseThrow(() -> new RecordNotFoundException("El id del reparto ingresado no corresponde a uno existente"));
        Empleado empleado = empleadoRepo.findById(idRepartidor).orElseThrow(() -> new RecordNotFoundException("El id del repartidor ingresado no corresponde a uno existente"));


        EstadoReparto pendienteEjecucion = estadoRepartoRepo.findByNombre("Pendiente de Ejecución");
        EstadoReparto pendienteAsignacion = estadoRepartoRepo.findByNombre("Pendiente de Asignación");

        if (!reparto.getEstadoReparto().equals(pendienteAsignacion) && !reparto.getEstadoReparto().equals(pendienteEjecucion)) {
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
    public RepartoAsignadoDTO iniciarReparto(Long idReparto) throws RecordNotFoundException, UserUnauthorizedException, ValidacionException {
        Persona persona = getUsuarioFromContext().getPersona();
        Empleado repartidor = (Empleado) persona;
        if (persona instanceof Cliente || !repartidor.getTipo().getNombre().equals("Repartidor")) {
            throw new UserUnauthorizedException("Esta funcionalidad es exclusiva para repartidores.");
        }
        Reparto reparto = repartoRepo.findById(idReparto).orElseThrow(() -> new RecordNotFoundException("El id del reparto ingresado no corresponde a uno existente"));
        if (reparto.getEstadoReparto().getId() != 2L) {
            throw new ValidacionException("El reparto debe estar pendiente de ejecución para ser iniciado.");
        }

        List<Reparto> repartosRepartidor = repartoRepo.findRepartosByRepartidorIdAndEstadoRepartoId(repartidor.getId(), 3L);
        if (!repartosRepartidor.isEmpty()) {
            throw new ValidacionException("No puede ejecutar un reparto si ya está ejecutando uno.");
        }

        EstadoReparto enEjecucion = estadoRepartoRepo.findByNombre("En Ejecución");

        reparto.setEstadoReparto(enEjecucion);
        reparto.setFechaHoraInicio(LocalDateTime.now());

        EstadoEntrega pendiente = estadoEntregaRepo.findByNombreEstadoEntrega("Pendiente");

        for (Entrega entrega : reparto.getEntregas()) {
            entrega.setEstadoEntrega(pendiente);
            Cliente cliente = entrega.getDomicilio().getCliente();
            if (cliente.getUsuario() != null && cliente.getUsuario().getValidado()) {
                emailService.sendRepartoIniciadoEmail(cliente.getUsuario().getDireccionEmail(), cliente.getNombre() + " " + cliente.getApellido(), cliente.getEmpresa().getNombre());
            }
        }

        repartoRepo.save(reparto);
        return RepartoAsignadoDTO.builder().id(reparto.getId()).ruta(reparto.getRuta().getNombre()).fechaHoraInicio(reparto.getFechaHoraInicio()).fechaEjecucion(reparto.getFechaEjecucion()).cantEntregas(reparto.getEntregas().stream().filter(entrega -> entrega.getEstadoEntrega().getId() == 1L || entrega.getEstadoEntrega().getId() == 2L).count()).build();
    }

    @Transactional
    public ListarRepartosDTO cancelarReparto(Long idReparto) throws ValidacionException, RecordNotFoundException {
        Reparto reparto = repartoRepo.findById(idReparto).orElseThrow(() -> new RecordNotFoundException("El ID del reparto ingresado no corresponde a uno existente"));
        Empresa empresa = ((Empleado) getUsuarioFromContext().getPersona()).getEmpresa();
        EstadoReparto enEjecucion = estadoRepartoRepo.findByNombre("En Ejecución");
        EstadoReparto cancelado = estadoRepartoRepo.findByNombre("Cancelado");
        EstadoReparto finalizado = estadoRepartoRepo.findByNombre("Finalizado");

        if (reparto.getEstadoReparto().equals(enEjecucion)) {
            throw new ValidacionException("No se puede cancelar un reparto que se encuentra En Ejecución");
        }
        if (reparto.getEstadoReparto().equals(cancelado)) {
            throw new ValidacionException("El reparto ya está cancelado");
        }
        if (reparto.getEstadoReparto().equals(finalizado)) {
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

        if (!reparto.getEstadoReparto().equals(enEjecucion)) {
            throw new ValidacionException("No se puede finalizar un reparto que no se encuentra en ejecución");
        }

        EstadoEntrega pendiente = estadoEntregaRepo.findByNombreEstadoEntrega("Pendiente");
        Integer cantEntregasPendientes = 0;
        for (Entrega entrega : reparto.getEntregas()) {
            if (entrega.getEstadoEntrega().equals(pendiente)) {
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

        if (observaciones == null || observaciones.isEmpty()) {
            errors.put("observaciones", "No puede tener observaciones en blanco.");
        }

        EstadoReparto enEjecucion = estadoRepartoRepo.findByNombre("En Ejecución");

        if (!reparto.getEstadoReparto().equals(enEjecucion)) {
            errors.put("root", "No se puede finalizar un reparto que no se encuentra en ejecución");
        }

        if (!errors.isEmpty()) {
            throw new EntidadNoValidaException(errors);
        }

        reparto.setObservaciones(observaciones);
        reparto.setFechaHoraFin(LocalDateTime.now());

        EstadoEntrega pendiente = estadoEntregaRepo.findByNombreEstadoEntrega("Pendiente");
        EstadoEntrega cancelada = estadoEntregaRepo.findByNombreEstadoEntrega("Cancelada");
        Integer cantEntregasPendientes = 0;
        for (Entrega entrega : reparto.getEntregas()) {
            if (entrega.getEstadoEntrega().equals(pendiente)) {
                entrega.setEstadoEntrega(cancelada);
                cantEntregasPendientes++;
            }
        }

        if (cantEntregasPendientes > 0) {
            reparto.setEstadoReparto(estadoRepartoRepo.findByNombre("Incompleto"));
        } else {
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

    public List<RepartoAsignadoDTO> getRepartosAsignados(Long estado) throws UserUnauthorizedException {
        Persona persona = getUsuarioFromContext().getPersona();
        Empleado repartidor = (Empleado) persona;
        if (persona instanceof Cliente || !repartidor.getTipo().getNombre().equals("Repartidor")) {
            throw new UserUnauthorizedException("Esta funcionalidad es exclusiva para repartidores.");
        }

        List<Reparto> repartos = repartoRepo.findRepartosAsignadosHoy(repartidor.getId(), estado);

        return repartos.stream().map(reparto -> RepartoAsignadoDTO.builder().id(reparto.getId()).ruta(reparto.getRuta().getNombre()).fechaEjecucion(reparto.getFechaEjecucion()).cantEntregas(reparto.getEntregas().stream().filter(entrega -> entrega.getEstadoEntrega().getId() == 1L || entrega.getEstadoEntrega().getId() == 2L).count()).fechaHoraInicio(reparto.getFechaHoraInicio()).build()).collect(Collectors.toList());
    }

    public List<EntregaMobileDTO> getProximaEntrega(Long idReparto) throws RecordNotFoundException, ValidacionException {
        Reparto reparto = repartoRepo.findById(idReparto).orElseThrow(() -> new RecordNotFoundException("El id del reparto ingresado no corresponde a uno existente"));
        if (reparto.getEstadoReparto().getId() != 3L) {
            throw new ValidacionException("El reparto no se encuentra en ejecución.");
        }

        List<Entrega> entregas = entregaRepo.findProximaEntrega(reparto.getId());
        if (entregas.isEmpty()) {
            return new ArrayList<EntregaMobileDTO>();
        }

        return entregas.stream()
                .map(entrega -> EntregaMobileDTO.builder()
                        .id(entrega.getId())
                        .ordenVisita(entrega.getOrdenVisita())
                        .nombreCliente(entrega.getDomicilio().getCliente().getNombre() + " " + entrega.getDomicilio().getCliente().getApellido())
                        .domicilio(formatAddress(entrega.getDomicilio().getCalle(), entrega.getDomicilio().getNumero(), entrega.getDomicilio().getPisoDepartamento()) + ", " + entrega.getDomicilio().getLocalidad())
                        .montoRecaudar(entregaRepo.getMontoTotalByEntrega(entrega.getId()))
                        .observaciones(entrega.getDomicilio().getObservaciones()).build()).collect(Collectors.toList());
    }

    public List<GoogleDirectionsDTO> getUbicaciones(Long idReparto) throws ValidacionException, RecordNotFoundException {
        Reparto reparto = repartoRepo.findById(idReparto).orElseThrow(() -> new RecordNotFoundException("El id del reparto ingresado no corresponde a uno existente"));
        if (reparto.getEstadoReparto().getId() != 3L) {
            throw new ValidacionException("El reparto no se encuentra en ejecución.");
        }

        GoogleDirectionsDTO inicio = GoogleDirectionsDTO.builder().latitude(reparto.getRepartidor().getEmpresa().getUbicacion().getLatitud()).longitude(reparto.getRepartidor().getEmpresa().getUbicacion().getLongitud()).build();
        List<GoogleDirectionsDTO> direcciones = new ArrayList<>();
        direcciones.add(inicio);
        List<Domicilio> domicilios = reparto.getEntregas().stream().filter(e -> e.getEstadoEntrega().getId() != 5L).sorted(Comparator.comparing(Entrega::getOrdenVisita)).map(e -> e.getDomicilio()).collect(Collectors.toList());
        for (Domicilio domicilio : domicilios) {
            direcciones.add(GoogleDirectionsDTO.builder().latitude(domicilio.getUbicacion().getLatitud()).longitude(domicilio.getUbicacion().getLongitud()).build());
        }
        return direcciones;
    }

    public List<EntregaMobileDTO> getEntregasEjecucion(Long idReparto) throws ValidacionException, RecordNotFoundException {
        Reparto reparto = repartoRepo.findById(idReparto).orElseThrow(() -> new RecordNotFoundException("El id del reparto ingresado no corresponde a uno existente"));
        if (reparto.getEstadoReparto().getId() != 3L) {
            throw new ValidacionException("El reparto no se encuentra en ejecución.");
        }

        List<Entrega> entregas = reparto.getEntregas().stream().sorted(Comparator.comparing(Entrega::getOrdenVisita)).collect(Collectors.toList());
        return entregas.stream().map(entrega -> {
            if (entrega.getEstadoEntrega().getNombreEstadoEntrega().equals("Programada") || entrega.getEstadoEntrega().getNombreEstadoEntrega().equals("Pendiente")) {
                return EntregaMobileDTO.builder().repartoId(entrega.getReparto().getId()).fechaEjecucion(entrega.getReparto().getFechaEjecucion()).id(entrega.getId()).ordenVisita(entrega.getOrdenVisita()).observaciones(entrega.getDomicilio().getObservaciones()).nombreCliente(entrega.getDomicilio().getCliente().getNombre() + " " + entrega.getDomicilio().getCliente().getApellido()).domicilio(formatAddress(entrega.getDomicilio().getCalle(), entrega.getDomicilio().getNumero(), entrega.getDomicilio().getPisoDepartamento()) + ", " + entrega.getDomicilio().getLocalidad()).montoRecaudar(entregaRepo.getMontoTotalByEntrega(entrega.getId())).estado(entrega.getEstadoEntrega().getNombreEstadoEntrega()).build();
            }

            if (entrega.getEstadoEntrega().getNombreEstadoEntrega().equals("Cancelada")) {
                return EntregaMobileDTO.builder().repartoId(entrega.getReparto().getId()).fechaEjecucion(entrega.getReparto().getFechaEjecucion()).id(entrega.getId()).ordenVisita(entrega.getOrdenVisita()).observacionesEntrega(entrega.getObservaciones()).nombreCliente(entrega.getDomicilio().getCliente().getNombre() + " " + entrega.getDomicilio().getCliente().getApellido()).domicilio(formatAddress(entrega.getDomicilio().getCalle(), entrega.getDomicilio().getNumero(), entrega.getDomicilio().getPisoDepartamento()) + ", " + entrega.getDomicilio().getLocalidad()).montoRecaudar(entregaRepo.getMontoTotalByEntrega(entrega.getId())).estado(entrega.getEstadoEntrega().getNombreEstadoEntrega()).build();
            }

            if (entrega.getEstadoEntrega().getNombreEstadoEntrega().equals("Ausente")) {
                return EntregaMobileDTO.builder().repartoId(entrega.getReparto().getId()).fechaHoraVisita(entrega.getFechaHoraVisita()).id(entrega.getId()).ordenVisita(entrega.getOrdenVisita()).observacionesEntrega(entrega.getObservaciones()).nombreCliente(entrega.getDomicilio().getCliente().getNombre() + " " + entrega.getDomicilio().getCliente().getApellido()).domicilio(formatAddress(entrega.getDomicilio().getCalle(), entrega.getDomicilio().getNumero(), entrega.getDomicilio().getPisoDepartamento()) + ", " + entrega.getDomicilio().getLocalidad()).montoRecaudar(entregaRepo.getMontoTotalByEntrega(entrega.getId())).estado(entrega.getEstadoEntrega().getNombreEstadoEntrega()).build();
            }

            EntregaMobileDTO response = new EntregaMobileDTO();
            response.setId(entrega.getId());
            response.setRepartoId(entrega.getReparto().getId());
            response.setFechaHoraVisita(entrega.getFechaHoraVisita());
            response.setEstado(entrega.getEstadoEntrega().getNombreEstadoEntrega());
            response.setDomicilio(formatAddress(entrega.getDomicilio().getCalle(), entrega.getDomicilio().getNumero(), entrega.getDomicilio().getPisoDepartamento()) + ", " + entrega.getDomicilio().getLocalidad());
            response.setNombreCliente(entrega.getDomicilio().getCliente().getNombre() + " " + entrega.getDomicilio().getCliente().getApellido());
            response.setMontoEntregado(entrega.getMonto());
            if (entrega.getPago() != null) {
                response.setMontoRecaudado(entrega.getPago().getTotal());
                response.setMedioPago(entrega.getPago().getMedioPago().getNombre());
            }
            response.setObservacionesEntrega(entrega.getObservaciones());
            return response;
        }).collect(Collectors.toList());
    }

    private String formatAddress(String calle, Integer numero, String piso) {
        StringBuilder formattedAddress = new StringBuilder(calle);

        if (numero != null) {
            formattedAddress.append(" ").append(numero);
        }

        if (piso != null) {
            formattedAddress.append(" ").append(piso);
        }

        return formattedAddress.toString();
    }

    public void actualizarUbicacion(Long idReparto, UbicacionDTO ubicacion) throws ValidacionException, RecordNotFoundException {
        Reparto reparto = repartoRepo.findById(idReparto).orElseThrow(() -> new RecordNotFoundException("El id del reparto ingresado no corresponde a uno existente"));
        if (reparto.getEstadoReparto().getId() != 3L) {
            throw new ValidacionException("El reparto no se encuentra en ejecución.");
        }

        if (reparto.getUbicacion() == null) {
            Ubicacion ubicacionReparto = new Ubicacion();
            ubicacionReparto.setLatitud(ubicacion.getLatitud());
            ubicacionReparto.setLongitud(ubicacion.getLongitud());
            reparto.setUbicacion(ubicacionReparto);
        } else {
            reparto.getUbicacion().setLatitud(ubicacion.getLatitud());
            reparto.getUbicacion().setLongitud(ubicacion.getLongitud());
        }

        repartoRepo.save(reparto);
    }

    public GoogleDirectionsDTO getUbicacionRepartidor(Long idReparto) throws ValidacionException, RecordNotFoundException {
        Reparto reparto = repartoRepo.findById(idReparto).orElseThrow(() -> new RecordNotFoundException("El id del reparto ingresado no corresponde a uno existente"));
        if (reparto.getEstadoReparto().getId() != 3L) {
            throw new ValidacionException("El reparto no se encuentra en ejecución.");
        }

        return GoogleDirectionsDTO.builder().latitude(reparto.getUbicacion().getLatitud()).longitude(reparto.getUbicacion().getLongitud()).build();
    }

    public List<ListarRepartoMobileDTO> listarRepartosMobile(Long ruta, Long estado, LocalDate fechaEjecucionDesde, LocalDate fechaEjecucionHasta) throws UserUnauthorizedException {
        Persona persona = getUsuarioFromContext().getPersona();
        Empleado repartidor = (Empleado) persona;
        if (persona instanceof Cliente || !repartidor.getTipo().getNombre().equals("Repartidor")) {
            throw new UserUnauthorizedException("Esta funcionalidad es exclusiva para repartidores.");
        }

        List<Reparto> repartos = repartoRepo.searchMobile(ruta, repartidor.getId(), estado, fechaEjecucionDesde, fechaEjecucionHasta);
        return repartos.stream().map(r -> ListarRepartoMobileDTO.builder().id(r.getId()).ruta(r.getRuta().getNombre()).cantEntregas(r.getEntregas().size()).fechaEjecucion(r.getFechaEjecucion()).estado(r.getEstadoReparto().getNombre()).build()).collect(Collectors.toList());
    }

    public List<EntregaMobileDTO> getEntregasMobile(Long idReparto) throws ValidacionException, RecordNotFoundException {
        Reparto reparto = repartoRepo.findById(idReparto).orElseThrow(() -> new RecordNotFoundException("El id del reparto ingresado no corresponde a uno existente"));

        List<Entrega> entregas = reparto.getEntregas().stream().sorted(Comparator.comparing(Entrega::getOrdenVisita)).collect(Collectors.toList());
        return entregas.stream().map(entrega -> {
            if (entrega.getEstadoEntrega().getNombreEstadoEntrega().equals("Programada") || entrega.getEstadoEntrega().getNombreEstadoEntrega().equals("Pendiente")) {
                return EntregaMobileDTO.builder().repartoId(entrega.getReparto().getId()).fechaEjecucion(entrega.getReparto().getFechaEjecucion()).id(entrega.getId()).ordenVisita(entrega.getOrdenVisita()).observaciones(entrega.getDomicilio().getObservaciones()).nombreCliente(entrega.getDomicilio().getCliente().getNombre() + " " + entrega.getDomicilio().getCliente().getApellido()).domicilio(formatAddress(entrega.getDomicilio().getCalle(), entrega.getDomicilio().getNumero(), entrega.getDomicilio().getPisoDepartamento()) + ", " + entrega.getDomicilio().getLocalidad()).montoRecaudar(entregaRepo.getMontoTotalByEntrega(entrega.getId())).estado(entrega.getEstadoEntrega().getNombreEstadoEntrega()).build();
            }

            if (entrega.getEstadoEntrega().getNombreEstadoEntrega().equals("Cancelada")) {
                return EntregaMobileDTO.builder().repartoId(entrega.getReparto().getId()).fechaEjecucion(entrega.getReparto().getFechaEjecucion()).id(entrega.getId()).ordenVisita(entrega.getOrdenVisita()).observacionesEntrega(entrega.getObservaciones()).nombreCliente(entrega.getDomicilio().getCliente().getNombre() + " " + entrega.getDomicilio().getCliente().getApellido()).domicilio(formatAddress(entrega.getDomicilio().getCalle(), entrega.getDomicilio().getNumero(), entrega.getDomicilio().getPisoDepartamento()) + ", " + entrega.getDomicilio().getLocalidad()).montoRecaudar(entregaRepo.getMontoTotalByEntrega(entrega.getId())).estado(entrega.getEstadoEntrega().getNombreEstadoEntrega()).build();
            }

            if (entrega.getEstadoEntrega().getNombreEstadoEntrega().equals("Ausente")) {
                return EntregaMobileDTO.builder().repartoId(entrega.getReparto().getId()).fechaHoraVisita(entrega.getFechaHoraVisita()).id(entrega.getId()).ordenVisita(entrega.getOrdenVisita()).observacionesEntrega(entrega.getObservaciones()).nombreCliente(entrega.getDomicilio().getCliente().getNombre() + " " + entrega.getDomicilio().getCliente().getApellido()).domicilio(formatAddress(entrega.getDomicilio().getCalle(), entrega.getDomicilio().getNumero(), entrega.getDomicilio().getPisoDepartamento()) + ", " + entrega.getDomicilio().getLocalidad()).montoRecaudar(entregaRepo.getMontoTotalByEntrega(entrega.getId())).estado(entrega.getEstadoEntrega().getNombreEstadoEntrega()).build();
            }

            EntregaMobileDTO response = new EntregaMobileDTO();
            response.setId(entrega.getId());
            response.setRepartoId(entrega.getReparto().getId());
            response.setFechaHoraVisita(entrega.getFechaHoraVisita());
            response.setEstado(entrega.getEstadoEntrega().getNombreEstadoEntrega());
            response.setDomicilio(formatAddress(entrega.getDomicilio().getCalle(), entrega.getDomicilio().getNumero(), entrega.getDomicilio().getPisoDepartamento()) + ", " + entrega.getDomicilio().getLocalidad());
            response.setNombreCliente(entrega.getDomicilio().getCliente().getNombre() + " " + entrega.getDomicilio().getCliente().getApellido());
            response.setMontoEntregado(entrega.getMonto());
            if (entrega.getPago() != null) {
                response.setMontoRecaudado(entrega.getPago().getTotal());
                response.setMedioPago(entrega.getPago().getMedioPago().getNombre());
            }
            response.setObservacionesEntrega(entrega.getObservaciones());
            return response;
        }).collect(Collectors.toList());
    }
}

