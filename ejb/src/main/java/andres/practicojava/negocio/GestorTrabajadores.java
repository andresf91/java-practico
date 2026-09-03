package andres.practicojava.negocio;

import java.time.LocalDate;
import java.util.List;

import andres.practicojava.modelo.TrabajadorSalud;

// operaciones de la capa de negocio: agregar, obtener y buscar
public interface GestorTrabajadores {

    // recibe los atributos sueltos para que sirva a cualquier capa de presentación
    TrabajadorSalud agregar(String numeroRegistroMSP, String nombreCompleto, String especialidad,
                            LocalDate fechaAlta, int aniosExperiencia, List<String> prestadores)
            throws ReglaNegocioException;

    List<TrabajadorSalud> obtenerTrabajadores();

    List<TrabajadorSalud> buscarPorEspecialidad(String especialidad);
}
