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
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.aquatrack_backend.model.Reparto;
import com.example.aquatrack_backend.repo.RepartoRepo;
import com.example.aquatrack_backend.repo.RepoBase;

@Service
public class RepartoServicio extends ServicioBaseImpl<Reparto> {

    @Autowired
    private RepartoRepo repo;

    @Autowired
    private RutaRepo rutaRepo;

    @Autowired
    private EstadoRepartoRepo estadoRepartoRepo;

    @Autowired
    private EstadoEntregaRepo estadoEntregaRepo;

    @Autowired
    private BingMapsConfig bingMapsConfig;

    @Autowired
    private RestTemplate restTemplate;

    private ModelMapper mapper = new ModelMapper();


    static {
        Loader.loadNativeLibraries();
    }

    public RepartoDTO crearReparto(Long id) throws RecordNotFoundException, ValidacionException {
        Ruta ruta = rutaRepo.findById(id).orElseThrow(() -> new RecordNotFoundException("La ruta no fue encontrada"));


        LocalDate now = LocalDate.now();


        DayOfWeek dayOfWeek = now.getDayOfWeek();


        Locale spanishLocale = new Locale("es", "ES");
        String nombreDia = dayOfWeek.getDisplayName(TextStyle.FULL, spanishLocale);


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
        if (entregasARepartir == null || entregasARepartir.isEmpty()){
            throw new ValidacionException("La ruta no contiene entregas a realizar");
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



  @Autowired
  private RepartoRepo repartoRepo;

  public RepartoServicio(RepoBase<Reparto> repoBase) {
    super(repoBase);
  }
}
