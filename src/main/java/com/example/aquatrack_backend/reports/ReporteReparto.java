package com.example.aquatrack_backend.reports;

import com.example.aquatrack_backend.exception.ValidacionException;
import com.example.aquatrack_backend.model.Domicilio;
import com.example.aquatrack_backend.model.Entrega;
import com.example.aquatrack_backend.model.EntregaDetalle;
import com.example.aquatrack_backend.model.Reparto;
import lombok.NoArgsConstructor;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

@NoArgsConstructor
public class ReporteReparto {

    public static ByteArrayInputStream generarReporte(Reparto reparto) throws ValidacionException {
        try (Workbook wb = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = wb.createSheet("Reparto " + reparto.getId());

            Font headerFont = wb.createFont();
            headerFont.setFontName("Arial");
            headerFont.setFontHeightInPoints((short) 18);
            headerFont.setColor(IndexedColors.BLUE.getIndex());

            CellStyle headerStyle = wb.createCellStyle();
            headerStyle.setFont(headerFont);

            Font dataFont = wb.createFont();
            dataFont.setFontName("Arial");
            dataFont.setFontHeightInPoints((short) 15);
            dataFont.setColor(IndexedColors.BLACK.getIndex());

            Font redFont = wb.createFont();
            redFont.setFontName("Arial");
            redFont.setFontHeightInPoints((short) 15);
            redFont.setColor(IndexedColors.RED.getIndex());

            CellStyle redStyle = wb.createCellStyle();
            redStyle.setFont(redFont);

            Font greenFont = wb.createFont();
            greenFont.setFontName("Arial");
            greenFont.setFontHeightInPoints((short) 15);
            greenFont.setColor(IndexedColors.GREEN.getIndex());

            CellStyle greenStyle = wb.createCellStyle();
            greenStyle.setFont(greenFont);

            CellStyle dataStyle = wb.createCellStyle();
            dataStyle.setFont(dataFont);

            String dia = reparto.getRuta().getDiaRutas().stream().filter(diaRuta -> diaRuta.getDiaSemana().getId().intValue() == (reparto.getFechaEjecucion().getDayOfWeek().getValue())).findFirst().get().getDiaSemana().getNombre();

            Row titleRow = sheet.createRow(0);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellStyle(headerStyle);
            titleCell.setCellValue("REPARTO #" + reparto.getId() + " - " + dia + ", " + reparto.getFechaEjecucion().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));

            sheet.createRow(1);

            Row routeRow = sheet.createRow(2);
            Cell routeCell = routeRow.createCell(0);
            routeCell.setCellValue("Ruta: " + reparto.getRuta().getNombre());
            routeCell.setCellStyle(dataStyle);

            Row employeeRow = sheet.createRow(3);
            Cell employeeCell = employeeRow.createCell(0);
            employeeCell.setCellValue("Repartidor: " + reparto.getRepartidor().getNombre() + " " + reparto.getRepartidor().getApellido());
            employeeCell.setCellStyle(dataStyle);


            Row startDateRow = sheet.createRow(4);
            Cell startDateCell = startDateRow.createCell(0);
            startDateCell.setCellValue("Hora de inicio: " + reparto.getFechaHoraInicio().format(DateTimeFormatter.ofPattern("HH:mm")) + "hs");
            startDateCell.setCellStyle(dataStyle);


            Row endDateRow = sheet.createRow(5);
            Cell endDateCell = endDateRow.createCell(0);
            endDateCell.setCellValue("Hora de finalización: " + reparto.getFechaHoraFin().format(DateTimeFormatter.ofPattern("HH:mm")) + "hs");
            endDateCell.setCellStyle(dataStyle);

            Row statusRow = sheet.createRow(6);
            Cell statusCell = statusRow.createCell(0);
            statusCell.setCellValue("Estado: " + reparto.getEstadoReparto().getNombre());
            statusCell.setCellStyle(dataStyle);

            sheet.createRow(7);

            Font dataFontBold = wb.createFont();
            dataFontBold.setFontName("Arial");
            dataFontBold.setFontHeightInPoints((short) 15);
            dataFontBold.setColor(IndexedColors.BLACK.getIndex());
            dataFontBold.setBold(true);

            CellStyle dataStyleBold = wb.createCellStyle();
            dataStyleBold.setFont(dataFontBold);

            Row tableHeaderRow = sheet.createRow(8);

            Cell entregaHeaderCell = tableHeaderRow.createCell(0);
            entregaHeaderCell.setCellStyle(dataStyleBold);
            entregaHeaderCell.setCellValue("Entrega");

            Cell clientHeaderCell = tableHeaderRow.createCell(1);
            clientHeaderCell.setCellStyle(dataStyleBold);
            clientHeaderCell.setCellValue("Cliente");

            Cell detailHeaderCell = tableHeaderRow.createCell(2);
            detailHeaderCell.setCellStyle(dataStyleBold);
            detailHeaderCell.setCellValue("Detalle");

            Cell totalHeaderCell = tableHeaderRow.createCell(3);
            totalHeaderCell.setCellStyle(dataStyleBold);
            totalHeaderCell.setCellValue("Total");

            Cell payHeaderCell = tableHeaderRow.createCell(4);
            payHeaderCell.setCellStyle(dataStyleBold);
            payHeaderCell.setCellValue("Pago");

            Cell balanceHeaderCell = tableHeaderRow.createCell(5);
            balanceHeaderCell.setCellStyle(dataStyleBold);
            balanceHeaderCell.setCellValue("Balance");

            Cell observacionesEntregaCell = tableHeaderRow.createCell(6);
            observacionesEntregaCell.setCellStyle(dataStyleBold);
            observacionesEntregaCell.setCellValue("Observaciones de la entrega");

            int index = 9;

            int cantidadAusentes = 0;
            BigDecimal cantRecaudada = BigDecimal.ZERO;
            BigDecimal balance = BigDecimal.ZERO;
            Boolean encontrado;
            for (Entrega entrega:reparto.getEntregas()) {
                if(clientePresente(entrega)){
                    encontrado = true;
                } else {
                    encontrado = false;
                    cantidadAusentes++;
                }
                Row entregaRow = sheet.createRow(index++);

                Cell entregaCell = entregaRow.createCell(0);
                entregaCell.setCellStyle(dataStyle);
                entregaCell.setCellValue(obtenerDescripcionDomicilio(entrega.getDomicilio()));

                Cell clientCell = entregaRow.createCell(1);
                clientCell.setCellStyle(dataStyle);
                clientCell.setCellValue(entrega.getDomicilio().getCliente().getNombre() + " " + entrega.getDomicilio().getCliente().getApellido());

                Cell detailCell = entregaRow.createCell(2);
                detailCell.setCellStyle(dataStyle);
                detailCell.setCellValue(obtenerDetalleEntrega(entrega));

                Cell totalCell = entregaRow.createCell(3);
                totalCell.setCellStyle(dataStyle);
//                totalCell.setCellValue(entrega.getMonto().toString());

                Cell payCell = entregaRow.createCell(4);
                payCell.setCellStyle(dataStyle);
//                payCell.setCellValue(entrega.getPago().getTotal().toString());

                Cell balanceCell = entregaRow.createCell(5);
                balanceCell.setCellStyle(dataStyle);
//                balanceCell.setCellValue(entrega.getPago().getDeudaPago().getMontoAdeudadoPago().toString());

                Cell observacionesEntrega = entregaRow.createCell(6);
                observacionesEntrega.setCellStyle(dataStyle);

                if (encontrado){
                    totalCell.setCellValue("$" + entrega.getMonto().toString());
                    payCell.setCellValue("$" + entrega.getPago().getTotal().toString());
                    observacionesEntrega.setCellValue(entrega.getObservaciones());
                    if (entrega.getPago().getDeudaPago().getMontoAdeudadoPago().compareTo(BigDecimal.ZERO) > 0){
                        balanceCell.setCellStyle(redStyle);
                        balanceCell.setCellValue("$" + entrega.getPago().getDeudaPago().getMontoAdeudadoPago().toString());
                    } else if (entrega.getPago().getDeudaPago().getMontoAdeudadoPago().compareTo(BigDecimal.ZERO) < 0){
                        balanceCell.setCellStyle(greenStyle);
                        balanceCell.setCellValue("$" + entrega.getPago().getDeudaPago().getMontoAdeudadoPago().negate().toString());
                    } else {
                        balanceCell.setCellValue("$" + entrega.getPago().getDeudaPago().getMontoAdeudadoPago().toString());
                    }
                    cantRecaudada = cantRecaudada.add(entrega.getPago().getTotal());
                    balance = balance.add(entrega.getPago().getDeudaPago().getMontoAdeudadoPago());
                } else {
                    totalCell.setCellValue(" - ");
                    payCell.setCellValue(" - ");
                    balanceCell.setCellValue(" - ");
                    observacionesEntrega.setCellValue(" - ");
                }

            }

            sheet.createRow(index++);

            Row summaryRow = sheet.createRow(index++);
            Cell summaryCell = summaryRow.createCell(0);
            summaryCell.setCellStyle(dataStyleBold);
            summaryCell.setCellValue("Resumen ");

            Row summaryTotalRow = sheet.createRow(index++);
            Cell summaryTotalCell = summaryTotalRow.createCell(0);
            summaryTotalCell.setCellStyle(dataStyle);
            summaryTotalCell.setCellValue("Total recaudado: $" + cantRecaudada);

            Row summaryDeuda = sheet.createRow(index++);
            Cell summaryDeudaCell = summaryDeuda.createCell(0);
            summaryDeudaCell.setCellStyle(dataStyle);
            summaryDeudaCell.setCellValue("Deuda a favor: $" + (balance.compareTo(BigDecimal.ZERO) > 0 ? balance.toString() : "0.00"));

            Row summaryEntregas = sheet.createRow(index++);
            Cell summaryEntregasCell = summaryEntregas.createCell(0);
            summaryEntregasCell.setCellStyle(dataStyle);
            summaryEntregasCell.setCellValue("Cantidad Entregas Realizadas: " + ( reparto.getEntregas().size() - cantidadAusentes + " de " + reparto.getEntregas().size() ));

            for (int i = 0; i < 6; i++) {
                sheet.autoSizeColumn(i);
            }

            sheet.createRow(index++);

            Font observationsFont = wb.createFont();
            observationsFont.setFontName("Arial");
            observationsFont.setFontHeightInPoints((short) 12);

            CellStyle observationsStyle = wb.createCellStyle();
            observationsStyle.setFont(observationsFont);
            observationsStyle.setWrapText(true);


            Row observationsRow = sheet.createRow(index);
            Cell observationsCell = observationsRow.createCell(0);
            observationsCell.setCellStyle(observationsStyle);
            observationsCell.setCellValue("Observaciones: " + reparto.getObservaciones());



            wb.write(out);

            ByteArrayInputStream stream = new ByteArrayInputStream(out.toByteArray());

            return stream;

        } catch (IOException exception){
            exception.printStackTrace();
            throw new ValidacionException("Hubo un problema al generar el reporte");
        }
    }

    private static String obtenerDetalleEntrega(Entrega entrega) {
        StringBuilder sb = new StringBuilder();
        if (entrega.getEstadoEntrega().getId() == 4){
            return sb.append("El cliente no se encontró en el domicilio").toString();
        }
        if (entrega.getEstadoEntrega().getId() == 5){
            return sb.append("El cliente notificó que no se iba a encontrar en el domicilio").toString();
        }
        for (EntregaDetalle detalle: entrega.getEntregaDetalles()) {
            sb.append(detalle.getProducto().getNombre()).append(": se entregó ").append(detalle.getCantidadEntregada()).append(" y se recibió ").append(detalle.getCantidadRecibida()).append(" - ");
        }
        return sb.toString();
    }

    public static String obtenerDescripcionDomicilio(Domicilio domicilio){
        //return domicilio.getCalle() + " " + nullableToEmptyString(domicilio.getNumero()) + " " + nullableToEmptyString(domicilio.getPisoDepartamento() + ", " + nullableToEmptyString(domicilio.getLocalidad()));
        return formatAddress(domicilio.getCalle(), domicilio.getNumero(), domicilio.getPisoDepartamento(), domicilio.getLocalidad());
    }

    private static String nullableToEmptyString(Object value) {
        if (value == null) {
            return "";
        } else {
            return value.toString();
        }
    }
    private static Boolean clientePresente(Entrega entrega){
        if (entrega.getEstadoEntrega().getId() == 4){
            return false;
        }
        if (entrega.getEstadoEntrega().getId() == 5){
            return false;
        }
        return true;
    }

    private static String formatAddress(String calle, Integer numero, String piso, String localidad) {
        StringBuilder formattedAddress = new StringBuilder(calle);

        if (numero != null) {
            formattedAddress.append(" ").append(numero);
        }

        if (piso != null) {
            formattedAddress.append(" ").append(piso);
        }

        if (localidad != null) {
            formattedAddress.append(" ").append(localidad);
        }

        return formattedAddress.toString();
    }
}
