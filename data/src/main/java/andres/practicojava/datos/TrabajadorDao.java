package andres.practicojava.datos;

import java.util.List;

import andres.practicojava.modelo.TrabajadorSalud;

public interface TrabajadorDao {

    TrabajadorSalud crear(TrabajadorSalud trabajador);

    // alta atómica, devuelve null si el registro MSP ya está tomado
    TrabajadorSalud crearSiNoExisteRegistro(TrabajadorSalud trabajador);

    List<TrabajadorSalud> listar();

    // null si no existe
    TrabajadorSalud buscarPorRegistroMSP(String numeroRegistroMSP);

    List<TrabajadorSalud> buscarPorEspecialidad(String especialidad);
}
