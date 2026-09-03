package andres.practicojava.cliente;

import jakarta.jms.ConnectionFactory;
import jakarta.jms.JMSContext;
import jakarta.jms.Queue;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

import andres.practicojava.mensajeria.MensajeAlta;
import andres.practicojava.modelo.TrabajadorSalud;
import andres.practicojava.negocio.GestorTrabajadoresRemote;
import andres.practicojava.negocio.ReglaNegocioException;

/** capa de presentación como aplicación Java de consola, cliente remoto del gestor */
public class ConsolaTrabajadores {

    // ejb:<app>/<módulo>//<clase del bean>!<interfaz remota>
    private static final String JNDI_GESTOR =
            "ejb:practicojava/practicojava-ejb//GestorTrabajadoresBean!"
            + "andres.practicojava.negocio.GestorTrabajadoresRemote";

    private static final String JNDI_FABRICA = "jms/RemoteConnectionFactory";

    public static void main(String[] args) throws Exception {
        InitialContext ctx = crearContexto();
        GestorTrabajadoresRemote gestor = (GestorTrabajadoresRemote) ctx.lookup(JNDI_GESTOR);
        Scanner in = new Scanner(System.in);

        while (true) {
            System.out.println("""

                    --- Gestor de Trabajadores de la Salud (cliente remoto) ---
                    1) Listar trabajadores
                    2) Buscar por especialidad
                    3) Agregar trabajador
                    4) Encolar alta (asincrónico)
                    0) Salir""");
            System.out.print("Opción: ");

            String opcion = in.hasNextLine() ? in.nextLine().trim() : "0";
            switch (opcion) {
                case "1" -> imprimir(gestor.obtenerTrabajadores());
                case "2" -> {
                    System.out.print("Especialidad: ");
                    imprimir(gestor.buscarPorEspecialidad(in.nextLine()));
                }
                case "3" -> agregar(gestor, in);
                case "4" -> encolarAlta(ctx, in);
                case "0" -> { System.out.println("Chau"); return; }
                default  -> System.out.println("Opción inválida");
            }
        }
    }

    /** conexión al directorio JNDI de WildFly, parametrizable por system properties */
    private static InitialContext crearContexto() throws NamingException {
    	
        Properties props = new Properties();
        
        props.put(Context.INITIAL_CONTEXT_FACTORY,
                  "org.wildfly.naming.client.WildFlyInitialContextFactory");
        
        props.put(Context.PROVIDER_URL, System.getProperty("tse.url", "remote+http://localhost:8080"));
        props.put(Context.SECURITY_PRINCIPAL, usuario());
        props.put(Context.SECURITY_CREDENTIALS, password());
        props.put("jboss.naming.client.ejb.context", "true");

        // el lookup devuelve un lazy proxy, no falla aunque el servidor este apagado
        return new InitialContext(props);
    }

    private static String usuario() {
        return System.getProperty("tse.user", "tse");
    }

    private static String password() {
        return System.getProperty("tse.pass", "Tse#2026");
    }

    private static void agregar(GestorTrabajadoresRemote gestor, Scanner in) {
        MensajeAlta datos = pedirDatos(in);
        if (datos == null) {
            return;
        }
        try {
            System.out.println("OK: " + gestor.agregar(datos.numeroRegistroMSP(), datos.nombreCompleto(),
                    datos.especialidad(), datos.fechaAlta(), datos.aniosExperiencia(), datos.prestadores()));

        } catch (ReglaNegocioException e) {
            System.out.println("Rechazado por regla de negocio: " + e.getMessage());
        }
    }

    // productor JMS remoto
    private static void encolarAlta(InitialContext ctx, Scanner in) {
        MensajeAlta datos = pedirDatos(in);
        if (datos == null) {
            return;
        }
        try {
            ConnectionFactory fabrica = (ConnectionFactory) ctx.lookup(JNDI_FABRICA);

            try (JMSContext jms = fabrica.createContext(usuario(), password())) {

                // createQueue resuelve la cola por su nombre en el proveedor
                Queue cola = jms.createQueue(MensajeAlta.NOMBRE_COLA);
                
                String mensaje = MensajeAlta.armar(datos.numeroRegistroMSP(), datos.nombreCompleto(),
                        datos.especialidad(), datos.fechaAlta(), datos.aniosExperiencia(), datos.prestadores());

                jms.createProducer().send(cola, mensaje);
                
                System.out.println("Encolado en " + MensajeAlta.NOMBRE_COLA + ": " + mensaje);
            }
        } catch (NamingException e) {
            System.out.println("No se pudo obtener la fábrica de conexiones JMS: " + e.getMessage());
        }
    }

    // null si los datos ingresados no se pueden convertir
    private static MensajeAlta pedirDatos(Scanner in) {
        
        try {
            System.out.print("Nº registro MSP: ");
            String registro = in.nextLine();
            System.out.print("Nombre completo: ");
            String nombre = in.nextLine();
            System.out.print("Especialidad: ");
            String especialidad = in.nextLine();
            System.out.print("Fecha de alta (AAAA-MM-DD): ");
            LocalDate fecha = LocalDate.parse(in.nextLine().trim());
            System.out.print("Años de experiencia: ");
            int anios = Integer.parseInt(in.nextLine().trim());
            System.out.print("Prestadores en los que trabaja (RUT separados por coma): ");
            List<String> prestadores = new ArrayList<>(List.of(in.nextLine().split(",")));

            return new MensajeAlta(registro, nombre, especialidad, fecha, anios, prestadores);

        } catch (DateTimeParseException e) {
            System.out.println("Fecha inválida. Formato esperado: AAAA-MM-DD");
        } catch (NumberFormatException e) {
            System.out.println("Los años de experiencia deben ser un número entero.");
        }
        return null;
    }

    private static void imprimir(List<TrabajadorSalud> trabajadores) {
        if (trabajadores.isEmpty()) {
            System.out.println("(sin resultados)");
            return;
        }
        trabajadores.forEach(t -> System.out.println("  " + t));
    }
}
