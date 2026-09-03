package andres.practicojava.cliente;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

import andres.practicojava.modelo.TrabajadorSalud;
import andres.practicojava.negocio.GestorTrabajadoresRemote;
import andres.practicojava.negocio.ReglaNegocioException;

/** capa de presentación como aplicación Java de consola, cliente remoto del gestor */
public class ConsolaTrabajadores {

    // ejb:<app>/<módulo>//<clase del bean>!<interfaz remota>
    private static final String JNDI_GESTOR =
            "ejb:practicojava/practicojava-ejb//GestorTrabajadoresBean!"
            + "andres.practicojava.negocio.GestorTrabajadoresRemote";

    public static void main(String[] args) throws Exception {
        GestorTrabajadoresRemote gestor = obtenerGestorRemoto();
        Scanner in = new Scanner(System.in);

        while (true) {
            System.out.println("""

                    --- Gestor de Trabajadores de la Salud (cliente remoto) ---
                    1) Listar trabajadores
                    2) Buscar por especialidad
                    3) Agregar trabajador
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
                case "0" -> { System.out.println("Chau"); return; }
                default  -> System.out.println("Opción inválida");
            }
        }
    }

    /** conexión al directorio JNDI de WildFly, parametrizable por system properties */
    private static GestorTrabajadoresRemote obtenerGestorRemoto() throws NamingException {
    	
        Properties props = new Properties();
        
        props.put(Context.INITIAL_CONTEXT_FACTORY,
                  "org.wildfly.naming.client.WildFlyInitialContextFactory");
        
        props.put(Context.PROVIDER_URL, System.getProperty("tse.url", "remote+http://localhost:8080"));
        props.put(Context.SECURITY_PRINCIPAL, System.getProperty("tse.user", "tse"));
        props.put(Context.SECURITY_CREDENTIALS, System.getProperty("tse.pass", "Tse#2026"));
        props.put("jboss.naming.client.ejb.context", "true");

        InitialContext ctx = new InitialContext(props);
        
        // el lookup devuelve un lazy proxy, no falla aunque el servidor este apagado
        return (GestorTrabajadoresRemote) ctx.lookup(JNDI_GESTOR);
    }

    private static void agregar(GestorTrabajadoresRemote gestor, Scanner in) {
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

            System.out.println("OK: " + gestor.agregar(registro, nombre, especialidad, fecha, anios, prestadores));

        } catch (ReglaNegocioException e) {
            System.out.println("Rechazado por regla de negocio: " + e.getMessage());
        } catch (DateTimeParseException e) {
            System.out.println("Fecha inválida. Formato esperado: AAAA-MM-DD");
        } catch (NumberFormatException e) {
            System.out.println("Los años de experiencia deben ser un número entero.");
        }
    }

    private static void imprimir(List<TrabajadorSalud> trabajadores) {
        if (trabajadores.isEmpty()) {
            System.out.println("(sin resultados)");
            return;
        }
        trabajadores.forEach(t -> System.out.println("  " + t));
    }
}
