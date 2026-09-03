package andres.practicojava.datos;

import java.util.List;

import andres.practicojava.modelo.TrabajadorSalud;

public interface TrabajadorDao {

    TrabajadorSalud crear(TrabajadorSalud trabajador);

    List<TrabajadorSalud> listar();

    // null si no existe
    TrabajadorSalud buscarPorRegistroMSP(String numeroRegistroMSP);

    List<TrabajadorSalud> buscarPorEspecialidad(String especialidad);
}
