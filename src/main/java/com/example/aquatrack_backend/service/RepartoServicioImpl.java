package com.example.aquatrack_backend.service;

import com.example.aquatrack_backend.config.BingMapsConfig;
import com.example.aquatrack_backend.model.*;
import com.example.aquatrack_backend.repo.*;
import com.google.ortools.Loader;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

@Service
public class RepartoServicioImpl extends ServicioBaseImpl<Reparto> implements ServicioBase<Reparto> {

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

    public RepartoServicioImpl(RepoBase<Reparto> repoBase) {
        super(repoBase);
    }

    static {
        Loader.loadNativeLibraries();
    }

    public Reparto crearReparto(Long id) {
        Ruta ruta = rutaRepo.findById(id).orElseThrow();


        LocalDateTime now = LocalDateTime.now();

        // Obtener el día de la semana actual
        DayOfWeek dayOfWeek = now.getDayOfWeek();

        // Obtener el nombre del día en español
        Locale spanishLocale = new Locale("es", "ES"); // Configurar el locale a español
        String nombreDia = dayOfWeek.getDisplayName(TextStyle.FULL, spanishLocale);


        List<DomicilioRuta> domiciliosARepartir = new ArrayList<>();

        domiciliosLoop:
        for (DomicilioRuta domicilio: ruta.getDomicilioRutas()) {
            for (DiaDomicilio dia: domicilio.getDomicilio().getDiaDomicilios()) {
                if (dia.getDiaRuta().getDiaSemana().getNombre().equals(nombreDia)){
                    domiciliosARepartir.add(domicilio);
                    continue domiciliosLoop;
                }
            }
        }

        EstadoReparto estadoAnticipado = estadoRepartoRepo.findByNombre("Anticipado");

        Reparto reparto = new Reparto();

        for (Reparto repartoExistente: ruta.getRepartos()) {
            if (repartoExistente.getEstadoReparto().equals(estadoAnticipado)){
                reparto = repartoExistente;
            }
        }

        List<Ubicacion> rutaOptima = calcularRutaOptima(domiciliosARepartir);

        EstadoReparto estado = estadoRepartoRepo.findByNombre("Pendiente de Asignación");
        EstadoEntrega estadoEntrega = estadoEntregaRepo.findByNombreEstadoEntrega("Programada");

        reparto.setRuta(ruta);
        reparto.setEstadoReparto(estado);
        reparto.setFechaEjecucion(now);

        List<Entrega> entregas = new ArrayList<>();

        for (int i = 0; i < rutaOptima.size(); i++) {
            Entrega entrega = new Entrega();
            entrega.setDomicilio(rutaOptima.get(i).getDomicilio());
            entrega.setEstadoEntrega(estadoEntrega);
            entrega.setReparto(reparto);
            entrega.setOrdenVisita(i);
            entregas.add(entrega);
        }

        return reparto;

    }

    private List<Ubicacion> calcularRutaOptima(List<DomicilioRuta> domicilioRutas) {
        String apiKey = bingMapsConfig.getApiKey();
        try {
        StringBuilder urlBuilder = new StringBuilder("https://dev.virtualearth.net/REST/v1/Routes/Driving?");

        String coordenadasInicio = null;
            for (int i = 0; i < domicilioRutas.size(); i++) {

                Domicilio domicilio1 = domicilioRutas.get(i).getDomicilio();
                String nombre = domicilio1.getDescripcion();
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


        List<Ubicacion> ubicacionesOrdenadas = new ArrayList<>();

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

                ubicacionesOrdenadas.add(domicilioRutas.get(indiceCoordenada).getDomicilio().getUbicacion());
            }
            ubicacionesOrdenadas.add(domicilioRutas.get(0).getDomicilio().getUbicacion());


        connection.disconnect();
            return ubicacionesOrdenadas;
        } catch (Exception e){
            System.out.println(e.getMessage());
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }

    }



}
