/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.cfp01_project;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

/**
 * Genera los archivos de datos de prueba para el proyecto HelpDesk IT.
 *
 * <p>Esta clase corresponde a la Entrega 1 (Semana 3). Su responsabilidad
 * es crear información pseudoaleatoria, coherente y relacionada que será
 * utilizada posteriormente por el programa de procesamiento de tickets.</p>
 *
 * <p>Archivos generados:</p>
 * <ul>
 *     <li>{@code data/tecnicos.csv}: información de los técnicos.</li>
 *     <li>{@code data/tickets.txt}: tickets cerrados asociados a técnicos.</li>
 * </ul>
 *
 * <p>El programa no solicita información al usuario.</p>
 *
 * @author Brayan Valencia
 * @version 1.0
 */
public class GenerateInfoFiles {

    /** Cantidad de técnicos que se generará para las pruebas. */
    private static final int TECHNICIANS_COUNT = 10;

    /** Cantidad de tickets cerrados que se generará para las pruebas. */
    private static final int TICKETS_COUNT = 100;

    /** Generador de números pseudoaleatorios. */
    private static final Random RANDOM = new Random();

    /** Formato utilizado para las fechas de apertura y cierre. */
    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    /** Directorio donde se almacenan los archivos generados. */
    private static final String DATA_DIRECTORY = "data";

    /** Nombres utilizados para generar técnicos coherentes. */
    private static final String[] FIRST_NAMES = {
        "Carlos", "Ana", "Luis", "Martha", "Diego",
        "Sofia", "Javier", "Elena", "Andres", "Laura",
        "Daniel", "Camila", "Mateo", "Valentina", "Sebastian"
    };

    /** Apellidos utilizados para generar técnicos coherentes. */
    private static final String[] LAST_NAMES = {
        "Gomez", "Rodriguez", "Perez", "Martinez", "Garcia",
        "Lopez", "Torres", "Hernandez", "Diaz", "Vargas",
        "Ramirez", "Castro", "Moreno", "Rojas", "Mendoza"
    };

    /** Especialidades IT disponibles para los técnicos. */
    private static final String[] SPECIALTIES = {
        "Soporte L1",
        "Redes",
        "Hardware",
        "Software",
        "Bases de Datos",
        "Seguridad",
        "Sistemas"
    };

    /** Prioridades válidas para los tickets. */
    private static final String[] PRIORITIES = {
        "BAJA", "MEDIA", "ALTA", "CRITICA"
    };

    /**
     * Punto de entrada del generador.
     *
     * @param args argumentos de línea de comandos; no son requeridos.
     */
    public static void main(String[] args) {
        try {
            createDataDirectory();
            createTechniciansFile(TECHNICIANS_COUNT);
            createTicketsFile(TICKETS_COUNT, TECHNICIANS_COUNT);

            System.out.println(
                    "ÉXITO: Archivos de prueba generados correctamente en la carpeta 'data'."
            );
            System.out.println(" - data/tecnicos.csv");
            System.out.println(" - data/tickets.txt");
        } catch (IOException exception) {
            System.err.println(
                    "ERROR: No fue posible generar los archivos: "
                    + exception.getMessage()
            );
        }
    }

    /**
     * Crea el directorio de datos si todavía no existe.
     *
     * @throws IOException si no es posible crear el directorio.
     */
    public static void createDataDirectory() throws IOException {
        File directory = new File(DATA_DIRECTORY);

        if (!directory.exists() && !directory.mkdirs()) {
            throw new IOException(
                    "No fue posible crear el directorio: " + DATA_DIRECTORY
            );
        }
    }

    /**
     * Genera el archivo maestro de técnicos.
     *
     * <p>Formato de cada registro:</p>
     * <pre>
     * idTecnico;nombre;apellido;especialidad
     * </pre>
     *
     * @param technicianCount cantidad de técnicos a generar.
     * @throws IOException si ocurre un error durante la escritura.
     * @throws IllegalArgumentException si la cantidad es menor que uno.
     */
    public static void createTechniciansFile(int technicianCount)
            throws IOException {

        validatePositiveCount(technicianCount, "technicianCount");

        File file = new File(DATA_DIRECTORY, "tecnicos.csv");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (int i = 1; i <= technicianCount; i++) {
                String technicianId = generateTechnicianId(i);
                String firstName = getRandomValue(FIRST_NAMES);
                String lastName = getRandomValue(LAST_NAMES);
                String specialty = getRandomValue(SPECIALTIES);

                writer.write(
                        String.format(
                                "%s;%s;%s;%s",
                                technicianId,
                                firstName,
                                lastName,
                                specialty
                        )
                );
                writer.newLine();
            }
        }
    }

    /**
     * Genera el archivo de tickets cerrados.
     *
     * <p>Formato de cada registro:</p>
     * <pre>
     * idTicket;idTecnico;fechaApertura;fechaCierre;prioridad
     * </pre>
     *
     * <p>Cada ticket recibe un técnico válido entre los técnicos generados
     * y una fecha de cierre posterior a la fecha de apertura.</p>
     *
     * @param ticketCount cantidad total de tickets a generar.
     * @param technicianCount cantidad de técnicos disponibles.
     * @throws IOException si ocurre un error durante la escritura.
     * @throws IllegalArgumentException si alguno de los valores es menor que uno.
     */
    public static void createTicketsFile(
            int ticketCount,
            int technicianCount) throws IOException {

        validatePositiveCount(ticketCount, "ticketCount");
        validatePositiveCount(technicianCount, "technicianCount");

        File file = new File(DATA_DIRECTORY, "tickets.txt");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (int i = 1; i <= ticketCount; i++) {
                String ticketId = generateTicketId(i);
                String technicianId = generateTechnicianId(
                        1 + RANDOM.nextInt(technicianCount)
                );

                LocalDateTime openingDate = generateOpeningDateTime();
                int resolutionMinutes = generateResolutionMinutes();
                LocalDateTime closingDate =
                        openingDate.plusMinutes(resolutionMinutes);
                String priority = getRandomValue(PRIORITIES);

                writer.write(
                        String.format(
                                "%s;%s;%s;%s;%s",
                                ticketId,
                                technicianId,
                                openingDate.format(DATE_TIME_FORMATTER),
                                closingDate.format(DATE_TIME_FORMATTER),
                                priority
                        )
                );
                writer.newLine();
            }
        }
    }

    /**
     * Genera un identificador único y legible para un técnico.
     *
     * @param number consecutivo del técnico.
     * @return identificador con formato T001, T002, etc.
     */
    private static String generateTechnicianId(int number) {
        return String.format("T%03d", number);
    }

    /**
     * Genera un identificador único y legible para un ticket.
     *
     * @param number consecutivo del ticket.
     * @return identificador con formato TK001, TK002, etc.
     */
    private static String generateTicketId(int number) {
        return String.format("TK%03d", number);
    }

    /**
     * Selecciona un elemento pseudoaleatorio de un arreglo.
     *
     * @param values arreglo de valores disponibles.
     * @return uno de los valores contenidos en el arreglo.
     */
    private static String getRandomValue(String[] values) {
        return values[RANDOM.nextInt(values.length)];
    }

    /**
     * Genera una fecha de apertura dentro de un periodo de prueba.
     *
     * @return fecha y hora pseudoaleatoria.
     */
    private static LocalDateTime generateOpeningDateTime() {
        int day = 1 + RANDOM.nextInt(15);
        int hour = 7 + RANDOM.nextInt(11);
        int minute = RANDOM.nextInt(60);

        return LocalDateTime.of(
                2026,
                9,
                day,
                hour,
                minute
        );
    }

    /**
     * Genera un tiempo de resolución entre 10 minutos y 8 horas.
     *
     * @return duración de resolución expresada en minutos.
     */
    private static int generateResolutionMinutes() {
        return 10 + RANDOM.nextInt(471);
    }

    /**
     * Valida que una cantidad sea positiva.
     *
     * @param count cantidad que se desea validar.
     * @param parameterName nombre del parámetro.
     * @throws IllegalArgumentException si la cantidad es menor que uno.
     */
    private static void validatePositiveCount(
            int count,
            String parameterName) {

        if (count < 1) {
            throw new IllegalArgumentException(
                    parameterName + " debe ser mayor que cero."
            );
        }
    }
}
