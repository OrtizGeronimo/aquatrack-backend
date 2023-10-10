package com.example.aquatrack_backend.service;

import com.example.aquatrack_backend.config.BingMapsConfig;
import com.example.aquatrack_backend.dto.RepartoDTO;
import com.example.aquatrack_backend.exception.RecordNotFoundException;
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
import org.springframework.data.domain.Sort;
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
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

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

    @Scheduled(cron = "0 * * * * 1-6")
    @Transactional
    public void generacionAutomaticaRepartos() throws RecordNotFoundException, ValidacionException {

        LocalTime now = LocalTime.now();
        LocalTime today = LocalTime.of(now.getHour(), now.getMinute());

        List<Empresa> empresas = empresaRepo.findAll();

        LocalDate date = LocalDate.now();
        DayOfWeek dayOfWeek = date.getDayOfWeek();

        Locale spanishLocale = new Locale("es", "ES");
/*
        String nombreDia = dayOfWeek.getDisplayName(TextStyle.FULL, spanishLocale);
*/
        String nombreDia = "miercoles";

        for (Empresa empresa: empresas) {
            if (empresa.getHoraGeneracionReparto().equals(today)){
                /*      List<Ruta> rutas = empresa
                        .getRutas()
                        .stream().filter(ruta -> ruta.getDiaRutas()
                                .stream().map(diaRuta -> diaRuta.getDiaSemana().getNombre())
                                .collect(Collectors.toList()).contains(nombreDia))
                        .collect(Collectors.toList());*/

                for(Ruta ruta: empresa.getRutas()) {
                    crearReparto(ruta.getId());
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

//    @Transactional
    public RepartoDTO crearReparto(Long id) throws RecordNotFoundException, ValidacionException {
        Ruta ruta = rutaRepo.findById(id).orElseThrow(() -> new RecordNotFoundException("La ruta no fue encontrada"));


        LocalDate now = LocalDate.now();


        DayOfWeek dayOfWeek = now.getDayOfWeek();


        Locale spanishLocale = new Locale("es", "ES");
        String nombreDia = dayOfWeek.getDisplayName(TextStyle.FULL, spanishLocale);
        nombreDia = "miercoles";

        List<Entrega> entregasARepartir = new ArrayList<>();

        domiciliosLoop:
        for (DomicilioRuta domicilio: ruta.getDomicilioRutas()) {
            for (DiaDomicilio dia: domicilio.getDomicilio().getDiaDomicilios()) {
                if (dia.getDiaRuta().getDiaSemana().getNombre().equalsIgnoreCase(nombreDia)){
                    Entrega entrega = new Entrega();
                    entrega.setDomicilio(domicilio.getDomicilio());
                    entregasARepartir.add(entrega);
                    continue domiciliosLoop;
                }
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
        List<Entrega> rutaOptima = calcularRutaOptima(entregasARepartir);
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

    private List<Entrega> calcularRutaOptima(List<Entrega> domicilioRutas) throws ValidacionException {
        String apiKey = bingMapsConfig.getApiKey();
        try {
        StringBuilder urlBuilder = new StringBuilder("https://dev.virtualearth.net/REST/v1/Routes/Driving?");

        String coordenadasInicio = null;
            for (int i = 0; i < domicilioRutas.size(); i++) {

                Domicilio domicilio1 = domicilioRutas.get(i).getDomicilio();
                double lat1 = domicilio1.getUbicacion().getLatitud();
                double lon1 = domicilio1.getUbicacion().getLongitud();
                String originLatitude = Double.toString(lat1).replace(',', '.');
                String originLongitude = Double.toString(lon1).replace(',', '.');

                String coordinates = originLatitude + "," + originLongitude;

                if (i == 0) {
                    coordenadasInicio = coordinates;
                    urlBuilder.append("wp." + (i + 1) + "=" + coordinates + "&");
                } else {
                    urlBuilder.append("wp." + (i + 1) + "=" + coordinates + "&");
                    if (i == domicilioRutas.size() - 1){
                        urlBuilder.append("wp." + (i + 2) + "=" + coordenadasInicio + "&");
                    }
                }
            }

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

            for (int i = 0; i < coordinates.length()-1; i++) {
                String coordenada = coordinates.getString(i);
                String[] partes = coordenada.split("\\.");

                String numeroString = partes[1];

                Integer indiceCoordenada = Integer.parseInt(numeroString);

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


    public Page<RepartoDTO> listarRepartos(String nombreRuta, Integer cantidadEntregaDesde, Integer cantidadEntregaHasta, Integer estado, int page, int size) throws RecordNotFoundException {

        Pageable pageable = PageRequest.of(page, size/*, Sort.by("er.id, ru.nombre")*/);

        Page<Reparto> repartos = repartoRepo.search(nombreRuta, cantidadEntregaDesde, cantidadEntregaHasta, estado, pageable);

        if (repartos == null || repartos.isEmpty()){
            throw new RecordNotFoundException("No se encontraron repartos");
        }

        return repartos.map(reparto -> mapper.map(reparto, RepartoDTO.class));

    }


    @Transactional
    public RepartoDTO crearRepartoManual(Long id) throws RecordNotFoundException, ValidacionException {
        Ruta ruta = rutaRepo.findById(id).orElseThrow(() -> new RecordNotFoundException("La ruta no fue encontrada"));

        EstadoReparto pendienteAsignacion = estadoRepartoRepo.findByNombre("Pendiente de Asignación");
        EstadoReparto pendienteEjecucion = estadoRepartoRepo.findByNombre("Pendiente de Ejecución");
        EstadoReparto enEjecucion = estadoRepartoRepo.findByNombre("En Ejecución");

        if (ruta.getRepartos() != null && !ruta.getRepartos().isEmpty()){
            LocalDate now = LocalDate.now();
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
    public RepartoDTO asignarRepartidor(Long idReparto, Long idRepartidor) throws RecordNotFoundException, ValidacionException {
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

        return mapper.map(reparto, RepartoDTO.class);

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
    public void cancelarReparto(Long idReparto) throws ValidacionException, RecordNotFoundException {
        Reparto reparto = repartoRepo.findById(idReparto).orElseThrow(() -> new RecordNotFoundException("El id del reparto ingresado no corresponde a uno existente"));

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

        repartoRepo.save(reparto);
    }


    @Transactional
    public void finalizarRepartoIncompleto(Long idReparto, String observaciones) throws ValidacionException, RecordNotFoundException {
        Reparto reparto = repartoRepo.findById(idReparto).orElseThrow(() -> new RecordNotFoundException("El id del reparto ingresado no corresponde a uno existente"));

        if (observaciones == null){
            throw new ValidacionException("Debe ingresar observaciones");
        }

        EstadoReparto enEjecucion = estadoRepartoRepo.findByNombre("En Ejecución");

        if (!reparto.getEstadoReparto().equals(enEjecucion)){
            throw new ValidacionException("No se puede finalizar un reparto que no se encuentra en ejecución");
        }

        EstadoReparto incompleto = estadoRepartoRepo.findByNombre("Incompleto");

        reparto.setObservaciones(observaciones);
        reparto.setEstadoReparto(incompleto);

        repartoRepo.save(reparto);

    }
}

