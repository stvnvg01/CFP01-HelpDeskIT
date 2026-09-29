package com.mycompany.cfp01_project;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Programa principal para procesar la información generada por
 * GenerateInfoFiles y calcular indicadores de desempeño para HelpDesk IT.
 *
 * Lee los archivos tecnicos.csv y tickets.txt, relaciona los tickets
 * con sus respectivos técnicos, calcula el tiempo de resolución y
 * genera el archivo de salida kpis.csv.
 *
 * @author Brayan Valencia
 * @version 1.0
 */
public class main {

    private static final String DATA_DIRECTORY = "data";
    private static final String TECHNICIANS_FILE = DATA_DIRECTORY + "/tecnicos.csv";
    private static final String TICKETS_FILE = DATA_DIRECTORY + "/tickets.txt";
    private static final String KPI_FILE = DATA_DIRECTORY + "/kpis.csv";

    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public static void main(String[] args) {

        System.out.println("========================================");
        System.out.println("       HELPDESK IT - PROCESAMIENTO");
        System.out.println("========================================");
        System.out.println();

        try {
            Map<String, TechnicianData> technicians = loadTechnicians();

            System.out.println("Técnicos cargados: " + technicians.size());
            System.out.println();

            int processedTickets = processTickets(technicians);

            System.out.println();
            System.out.println("========================================");
            System.out.println("             RESULTADOS");
            System.out.println("========================================");
            System.out.println();

            System.out.println("Tickets procesados correctamente: "
                    + processedTickets);

            generateKpiFile(technicians);

            System.out.println();
            System.out.println("Reporte generado correctamente:");
            System.out.println(KPI_FILE);

            System.out.println();
            System.out.println("========================================");
            System.out.println("     PROCESO FINALIZADO CON ÉXITO");
            System.out.println("========================================");

        } catch (IOException | RuntimeException e) {

            System.err.println();
            System.err.println("========================================");
            System.err.println("              ERROR");
            System.err.println("========================================");
            System.err.println("No fue posible completar el procesamiento.");
            System.err.println("Detalle: " + e.getMessage());
        }
    }

    /**
     * Lee el archivo de técnicos y crea una estructura en memoria
     * para facilitar la relación entre técnicos y tickets.
     *
     * @return mapa de técnicos indexado por su identificador
     * @throws IOException si ocurre un error durante la lectura
     */
    private static Map<String, TechnicianData> loadTechnicians()
            throws IOException {

        Map<String, TechnicianData> technicians = new LinkedHashMap<>();

        File file = new File(TECHNICIANS_FILE);

        if (!file.exists()) {
            throw new IOException(
                    "No existe el archivo " + TECHNICIANS_FILE);
        }

        try (BufferedReader reader = new BufferedReader(
                new FileReader(file))) {

            String line;

            while ((line = reader.readLine()) != null) {

                line = line.trim();

                if (line.isEmpty()) {
                    continue;
                }

                String[] parts = line.split(";");

                if (parts.length != 4) {
                    throw new IOException(
                            "Formato inválido en tecnicos.csv: " + line);
                }

                String id = parts[0].trim();
                String name = parts[1].trim();
                String lastName = parts[2].trim();
                String specialty = parts[3].trim();

                if (technicians.containsKey(id)) {
                    throw new IOException(
                            "Identificador de técnico duplicado: " + id);
                }

                technicians.put(
                        id,
                        new TechnicianData(
                                id,
                                name,
                                lastName,
                                specialty
                        )
                );
            }
        }

        return technicians;
    }

    /**
     * Lee y procesa todos los tickets del archivo tickets.txt.
     *
     * @param technicians técnicos disponibles
     * @return cantidad de tickets procesados correctamente
     * @throws IOException si existe un problema con el archivo
     */
    private static int processTickets(
            Map<String, TechnicianData> technicians)
            throws IOException {

        File file = new File(TICKETS_FILE);

        if (!file.exists()) {
            throw new IOException(
                    "No existe el archivo " + TICKETS_FILE);
        }

        int processedTickets = 0;

        try (BufferedReader reader = new BufferedReader(
                new FileReader(file))) {

            String line;

            while ((line = reader.readLine()) != null) {

                line = line.trim();

                if (line.isEmpty()) {
                    continue;
                }

                String[] parts = line.split(";");

                if (parts.length != 5) {
                    throw new IOException(
                            "Formato inválido en tickets.txt: " + line);
                }

                String ticketId = parts[0].trim();
                String technicianId = parts[1].trim();
                String openingDate = parts[2].trim();
                String closingDate = parts[3].trim();
                String priority = parts[4].trim();

                TechnicianData technician =
                        technicians.get(technicianId);

                if (technician == null) {
                    throw new IOException(
                            "El ticket " + ticketId
                            + " referencia un técnico inexistente: "
                            + technicianId);
                }

                LocalDateTime opening =
                        LocalDateTime.parse(
                                openingDate,
                                DATE_FORMAT
                        );

                LocalDateTime closing =
                        LocalDateTime.parse(
                                closingDate,
                                DATE_FORMAT
                        );

                long resolutionMinutes =
                        Duration.between(opening, closing).toMinutes();

                if (resolutionMinutes < 0) {
                    throw new IOException(
                            "El ticket " + ticketId
                            + " tiene una fecha de cierre anterior "
                            + "a la fecha de apertura."
                    );
                }

                technician.addTicket(
                        resolutionMinutes,
                        priority
                );

                processedTickets++;
            }
        }

        return processedTickets;
    }

    /**
     * Genera el archivo CSV con los indicadores de cada técnico.
     *
     * @param technicians técnicos procesados
     * @throws IOException si ocurre un error de escritura
     */
    private static void generateKpiFile(
            Map<String, TechnicianData> technicians)
            throws IOException {

        File directory = new File(DATA_DIRECTORY);

        if (!directory.exists() && !directory.mkdirs()) {
            throw new IOException(
                    "No fue posible crear la carpeta data."
            );
        }

        try (BufferedWriter writer = new BufferedWriter(
                new FileWriter(KPI_FILE))) {

            writer.write(
                    "idTecnico;nombre;apellido;especialidad;"
                    + "ticketsResueltos;tiempoTotalMinutos;"
                    + "tiempoPromedioMinutos"
            );

            writer.newLine();

            for (TechnicianData technician : technicians.values()) {

                writer.write(
                        technician.toCsv()
                );

                writer.newLine();
            }
        }
    }

    /**
     * Clase interna que representa la información acumulada
     * de un técnico.
     */
    private static class TechnicianData {

        private final String id;
        private final String name;
        private final String lastName;
        private final String specialty;

        private int resolvedTickets;
        private long totalResolutionMinutes;

        public TechnicianData(
                String id,
                String name,
                String lastName,
                String specialty) {

            this.id = id;
            this.name = name;
            this.lastName = lastName;
            this.specialty = specialty;
            this.resolvedTickets = 0;
            this.totalResolutionMinutes = 0;
        }

        public void addTicket(
                long resolutionMinutes,
                String priority) {

            resolvedTickets++;
            totalResolutionMinutes += resolutionMinutes;
        }

        public double getAverageResolutionMinutes() {

            if (resolvedTickets == 0) {
                return 0.0;
            }

            return (double) totalResolutionMinutes
                    / resolvedTickets;
        }

        public String toCsv() {

            return String.format(
                    "%s;%s;%s;%s;%d;%d;%.2f",
                    id,
                    name,
                    lastName,
                    specialty,
                    resolvedTickets,
                    totalResolutionMinutes,
                    getAverageResolutionMinutes()
            );
        }
    }
}