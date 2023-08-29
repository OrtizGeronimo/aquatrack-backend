package com.example.aquatrack_backend.service;

import com.example.aquatrack_backend.model.*;
import com.example.aquatrack_backend.repo.RepartoRepo;
import com.example.aquatrack_backend.repo.RepoBase;
import com.example.aquatrack_backend.repo.RutaRepo;
import com.google.ortools.Loader;
import com.google.ortools.constraintsolver.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RepartoServicioImpl extends ServicioBaseImpl<Reparto> implements ServicioBase<Reparto> {

    @Autowired
    private RepartoRepo repo;

    @Autowired
    private RutaRepo rutaRepo;

    public RepartoServicioImpl(RepoBase<Reparto> repoBase) {
        super(repoBase);
    }

    static {
        Loader.loadNativeLibraries();
    }

    public int[] crearReparto(Long id) {
        Ruta ruta = rutaRepo.findById(id).orElseThrow();

        double[][] matrizDistancias = calcularMatrizDistancias(ruta.getDomicilioRutas());
        RoutingIndexManager manager = new RoutingIndexManager(ruta.getDomicilioRutas().size(), 1,0);
        RoutingModel routingModel = new RoutingModel(manager);

        final int transitCallbackIndex =
                routingModel.registerTransitCallback((long fromIndex, long toIndex) -> {
                    // Convert from routing variable Index to user NodeIndex.
                    int fromNode = manager.indexToNode(fromIndex);
                    int toNode = manager.indexToNode(toIndex);
                    return (long) matrizDistancias[fromNode][toNode];
                });


        routingModel.setArcCostEvaluatorOfAllVehicles(transitCallbackIndex);

        RoutingSearchParameters searchParameters =
                main.defaultRoutingSearchParameters()
                        .toBuilder()
                        .setFirstSolutionStrategy(FirstSolutionStrategy.Value.PATH_CHEAPEST_ARC)
                        .build();

        // Solve the TSP
        com.google.ortools.constraintsolver.Assignment solution = routingModel.solveWithParameters(searchParameters);

        // Get the optimized order as indices
        int[] tourIndices = new int[ruta.getDomicilioRutas().size()];
        long index = routingModel.start(0); // Start at the depot (location 0)
        int i = 0;
        while (!routingModel.isEnd(index)) {
            tourIndices[i++] = manager.indexToNode(index);
            index = solution.value(routingModel.nextVar(index));
        }

        return tourIndices;

    }

    private double[][] calcularMatrizDistancias(List<DomicilioRuta> domicilioRutas) {

        int cantDomicilios = domicilioRutas.size();
        double[][] matrizDistancias = new double[cantDomicilios][cantDomicilios];

        for (int i = 0; i < cantDomicilios; i++) {
            Domicilio domicilio1 = domicilioRutas.get(i).getDomicilio();
            double lat1 = domicilio1.getUbicacion().getLatitud();
            double lon1 = domicilio1.getUbicacion().getLongitud();

            for (int j = 0; j < cantDomicilios; j++) {
                if (i == j) {
                    matrizDistancias[i][j] = 0.0; // Si es la misma distancia entonces 0
                } else {
                    Domicilio domicilio2 = domicilioRutas.get(j).getDomicilio();
                    double lat2 = domicilio2.getUbicacion().getLatitud();
                    double lon2 = domicilio2.getUbicacion().getLongitud();

                    double distancia = calcularDistancia(lat1, lon1, lat2, lon2);
                    matrizDistancias[i][j] = distancia;
                }
            }
        }
        return matrizDistancias;
    }

    private double calcularDistancia(double lat1, double lon1, double lat2, double lon2) {

        lat1 = Math.toRadians(lat1);
        lon1 = Math.toRadians(lon1);
        lat2 = Math.toRadians(lat2);
        lon2 = Math.toRadians(lon2);

        // Formula Haversine
        double dlon = lon2 - lon1;
        double dlat = lat2 - lat1;
        double a = Math.sin(dlat / 2) * Math.sin(dlat / 2) + Math.cos(lat1) * Math.cos(lat2) * Math.sin(dlon / 2) * Math.sin(dlon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = 6371.0 * c; //6371 radio de la tierra en km

        return distance;
    }
}
