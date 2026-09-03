package andres.practicojava.mensajeria;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;


// contrato de la cola de altas, nombre del destino y formato del mensaje de texto plano
// numeroRegistroMSP|nombreCompleto|especialidad|fechaAlta|aniosExperiencia|prestadores

public record MensajeAlta(String numeroRegistroMSP, String nombreCompleto, String especialidad,
                          LocalDate fechaAlta, int aniosExperiencia, List<String> prestadores) {

    public static final String NOMBRE_COLA = "queue_alta_trabajador_salud";

    public static final String JNDI_COLA = "java:/jms/queue/" + NOMBRE_COLA;

    private static final String SEPARADOR = "|";
    private static final String SEPARADOR_PRESTADORES = ",";
    private static final int CANTIDAD_CAMPOS = 6;

    public static String armar(String numeroRegistroMSP, String nombreCompleto, String especialidad,
                               LocalDate fechaAlta, int aniosExperiencia, List<String> prestadores) {

        return String.join(SEPARADOR,
                texto(numeroRegistroMSP),
                texto(nombreCompleto),
                texto(especialidad),
                fechaAlta == null ? "" : fechaAlta.toString(),
                String.valueOf(aniosExperiencia),
                prestadores == null ? "" : String.join(SEPARADOR_PRESTADORES, prestadores));
    }

    // IllegalArgumentException si el texto no respeta el formato
    public static MensajeAlta parsear(String texto) {

        if (texto == null || texto.isBlank()) {
            throw new IllegalArgumentException("El mensaje viene vacío.");
        }

        String[] campos = texto.split("\\" + SEPARADOR, -1);
        if (campos.length != CANTIDAD_CAMPOS) {
            throw new IllegalArgumentException("El mensaje tiene " + campos.length
                    + " campos y se esperaban " + CANTIDAD_CAMPOS + ".");
        }

        LocalDate fechaAlta;
        try {
            fechaAlta = LocalDate.parse(campos[3].trim());
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Fecha de alta inválida: " + campos[3]);
        }

        int aniosExperiencia;
        try {
            aniosExperiencia = Integer.parseInt(campos[4].trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Años de experiencia inválidos: " + campos[4]);
        }

        return new MensajeAlta(campos[0].trim(), campos[1].trim(), campos[2].trim(),
                fechaAlta, aniosExperiencia, prestadoresDesde(campos[5]));
    }

    private static List<String> prestadoresDesde(String campo) {
        List<String> resultado = new ArrayList<>();
        for (String prestador : campo.split(SEPARADOR_PRESTADORES)) {
            if (!prestador.isBlank()) {
                resultado.add(prestador.trim());
            }
        }
        return resultado;
    }

    private static String texto(String valor) {
        return valor == null ? "" : valor.trim();
    }
}
