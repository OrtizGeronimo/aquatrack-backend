package com.example.aquatrack_backend.service;

import com.example.aquatrack_backend.config.*;
import com.example.aquatrack_backend.config.DistanceMatrixResponse;
import com.example.aquatrack_backend.model.Domicilio;
import com.example.aquatrack_backend.model.DomicilioRuta;
import com.example.aquatrack_backend.model.Reparto;
import com.example.aquatrack_backend.model.Ruta;
import com.example.aquatrack_backend.repo.RepartoRepo;
import com.example.aquatrack_backend.repo.RepoBase;
import com.example.aquatrack_backend.repo.RutaRepo;
import com.google.ortools.Loader;
import com.google.ortools.constraintsolver.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Service
public class RepartoServicioImpl extends ServicioBaseImpl<Reparto> implements ServicioBase<Reparto> {

    @Autowired
    private RepartoRepo repo;

    @Autowired
    private RutaRepo rutaRepo;

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

    public List<String> crearReparto(Long id) {
        Ruta ruta = rutaRepo.findById(id).orElseThrow();

        double[][] matrizDistancias = calcularMatrizDistancias(ruta.getDomicilioRutas());
//        RoutingIndexManager manager = new RoutingIndexManager(ruta.getDomicilioRutas().size(), 1,0);
//        RoutingModel routingModel = new RoutingModel(manager);
//
//        final int transitCallbackIndex =
//                routingModel.registerTransitCallback((long fromIndex, long toIndex) -> {
//                    // Convert from routing variable Index to user NodeIndex.
//                    int fromNode = manager.indexToNode(fromIndex);
//                    int toNode = manager.indexToNode(toIndex);
//                    return (long) matrizDistancias[fromNode][toNode];
//                });
//
//
//        routingModel.setArcCostEvaluatorOfAllVehicles(transitCallbackIndex);
//
//        RoutingSearchParameters searchParameters =
//                main.defaultRoutingSearchParameters()
//                        .toBuilder()
//                        .setFirstSolutionStrategy(FirstSolutionStrategy.Value.PATH_CHEAPEST_ARC)
//                        .build();
//
//        // Solve the TSP
//        com.google.ortools.constraintsolver.Assignment solution = routingModel.solveWithParameters(searchParameters);
//
//        // Get the optimized order as indices
//        int[] tourIndices = new int[ruta.getDomicilioRutas().size()];
//        long index = routingModel.start(0); // Start at the depot (location 0)
//        int i = 0;
//        while (!routingModel.isEnd(index)) {
//            tourIndices[i++] = manager.indexToNode(index);
//            index = solution.value(routingModel.nextVar(index));
//        }
//
//        List<String> domicilios = new ArrayList<>();
//
//        for (int indice : tourIndices) {
//            domicilios.add(ruta.getDomicilioRutas().get(indice).getDomicilio().getDescripcion());
//        }

        return null;

    }

    private double[][] calcularMatrizDistancias(List<DomicilioRuta> domicilioRutas) {
        String apiKey = bingMapsConfig.getApiKey();
        try{
        StringBuilder urlBuilder = new StringBuilder("https://dev.virtualearth.net/REST/v1/Routes/Driving?");
        for (int i = 0; i < domicilioRutas.size(); i++) {

            Domicilio domicilio1 = domicilioRutas.get(i).getDomicilio();
            double lat1 = domicilio1.getUbicacion().getLatitud();
            double lon1 = domicilio1.getUbicacion().getLongitud();
            String originLatitude = Double.toString(lat1).replace(',', '.');
            String originLongitude = Double.toString(lon1).replace(',', '.');

            String coordinates = originLatitude + "," + originLongitude;

            urlBuilder.append("waypoint." + (i + 1) + "=" + coordinates + "&");
//            }
        }
        urlBuilder.append("key=" + apiKey);

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

        // Parse the response and extract the shortest path information
        String jsonResponse = response.toString();
        // Parse the JSON response here and extract the shortest path data

        // Close the connection
        connection.disconnect();
        } catch (Exception e){
            System.out.println(e.getMessage());
        e.printStackTrace();
        }
        return null;
//        int cantDomicilios = domicilioRutas.size();
//        double[][] matrizDistancias = new double[cantDomicilios][cantDomicilios];
//
//        for (int i = 0; i < cantDomicilios; i++) {
//            Domicilio domicilio1 = domicilioRutas.get(i).getDomicilio();
//            double lat1 = domicilio1.getUbicacion().getLatitud();
//            double lon1 = domicilio1.getUbicacion().getLongitud();
//
//            for (int j = 0; j < cantDomicilios; j++) {
//                if (i == j) {
//                    matrizDistancias[i][j] = 0.0; // Si es la misma distancia entonces 0
//                } else {
//                    Domicilio domicilio2 = domicilioRutas.get(j).getDomicilio();
//                    double lat2 = domicilio2.getUbicacion().getLatitud();
//                    double lon2 = domicilio2.getUbicacion().getLongitud();
//
//                    double distancia = calculateDistanceAndTime(lat1, lon1, lat2, lon2);
//                    matrizDistancias[i][j] = distancia;
//                }
//            }
//        }
//        return matrizDistancias;
    }

//    public double calculateDistanceAndTime(double originLat, double originLng, double destLat, double destLng) {
//        String apiKey = bingMapsConfig.getApiKey();
//        String originLatitude = Double.toString(originLat).replace(',','.');
//        String originLongitude = Double.toString(originLng).replace(',','.');
//        String destinationLatitude = Double.toString(destLat).replace(',','.');
//        String destinationLongitude = Double.toString(destLng).replace(',','.');
//
//        String originCoordinates = originLatitude + ", " + originLongitude;
//        String destinationCoordinates = destinationLatitude + ", " + destinationLongitude;
//
//
//
//        String url = String.format(
//                "https://dev.virtualearth.net/REST/v1/Routes/DistanceMatrix?key=%s&origins=%s&destinations=%s&travelMode=driving&timeUnits=minutes",
//                apiKey, originCoordinates, destinationCoordinates
//        );
//
//        // Make an HTTP GET request to Bing Maps API
//        DistanceMatrixResponse response = restTemplate.getForObject(url, DistanceMatrixResponse.class);
//
//        // Extract distance and time information from the response
//        double distanceInMeters = response.getResourceSets().get(0).getResources().get(0).getResults().get(0).getTravelDistance();
//        double travelTimeInMinutes = response.getResourceSets().get(0).getResources().get(0).getResults().get(0).getTravelDuration();
//
//        return travelTimeInMinutes;
//    }
//
//    private double calcularDistancia(double lat1, double lon1, double lat2, double lon2) {
//
//        lat1 = Math.toRadians(lat1);
//        lon1 = Math.toRadians(lon1);
//        lat2 = Math.toRadians(lat2);
//        lon2 = Math.toRadians(lon2);
//
//        // Formula Haversine
//        double dlon = lon2 - lon1;
//        double dlat = lat2 - lat1;
//        double a = Math.sin(dlat / 2) * Math.sin(dlat / 2) + Math.cos(lat1) * Math.cos(lat2) * Math.sin(dlon / 2) * Math.sin(dlon / 2);
//        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
//        double distance = 6371.0 * c; //6371 radio de la tierra en km
//
//        return distance;
//    }
}
