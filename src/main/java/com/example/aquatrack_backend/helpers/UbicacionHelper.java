package com.example.aquatrack_backend.helpers;

import com.example.aquatrack_backend.dto.UbicacionDTO;
import com.example.aquatrack_backend.model.Cobertura;
import com.example.aquatrack_backend.model.Ubicacion;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Data
public class UbicacionHelper {

    public boolean estaContenida(UbicacionDTO ubiCliente, Cobertura cobertura){
            List<Coordinate> vertices = new ArrayList<>();
            List<Ubicacion> ubicaciones = cobertura.getUbicaciones();
            int i;
            for(i = 0; i<ubicaciones.size(); i++){
                Coordinate vertice = new Coordinate(ubicaciones.get(i).getLatitud(), ubicaciones.get(i).getLongitud());
                vertices.add(vertice);
            }
            Coordinate vertice = vertices.get(0);
            vertices.add(vertice);
            GeometryFactory geometryFactory = new GeometryFactory();
            Polygon polygon = geometryFactory.createPolygon(vertices.toArray(new Coordinate[0]));

            Point point = geometryFactory.createPoint(new Coordinate(ubiCliente.getLatitud(), ubiCliente.getLongitud()));
            boolean estaContenida = polygon.contains(point);

            return estaContenida;
    }
}
