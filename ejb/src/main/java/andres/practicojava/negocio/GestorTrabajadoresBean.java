package andres.practicojava.negocio;

import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import andres.practicojava.datos.TrabajadorDaoLocal;
import andres.practicojava.modelo.TrabajadorSalud;

// capa de negocio: valida las reglas de alta y delega en la capa de datos
@Stateless
public class GestorTrabajadoresBean implements GestorTrabajadoresLocal, GestorTrabajadoresRemote {

    private static final Logger LOG = Logger.getLogger(GestorTrabajadoresBean.class.getName());

    private static final int MAX_ANIOS_EXPERIENCIA = 60;

    // interfaz local, el singleton corre en la misma jvm
    @EJB
    private TrabajadorDaoLocal dao;

    @Override
    public TrabajadorSalud agregar(String numeroRegistroMSP, String nombreCompleto, String especialidad,
                                   LocalDate fechaAlta, int aniosExperiencia, List<String> prestadores)
            throws ReglaNegocioException {

        LOG.info("agregar() atendido por la instancia " + System.identityHashCode(this));

        // validaciones de forma
        if (esVacio(numeroRegistroMSP)) {
            throw new ReglaNegocioException("El número de registro MSP es obligatorio.");
        }
        if (esVacio(nombreCompleto)) {
            throw new ReglaNegocioException("El nombre completo es obligatorio.");
        }
        if (esVacio(especialidad)) {
            throw new ReglaNegocioException("La especialidad es obligatoria.");
        }
        if (fechaAlta == null) {
            throw new ReglaNegocioException("La fecha de alta es obligatoria.");
        }

        // regla 2: la fecha de alta no puede ser futura
        if (fechaAlta.isAfter(LocalDate.now())) {
            throw new ReglaNegocioException("La fecha de alta no puede ser posterior a hoy.");
        }

        // regla 3: los años de experiencia tienen que ser un valor plausible
        if (aniosExperiencia < 0 || aniosExperiencia > MAX_ANIOS_EXPERIENCIA) {
            throw new ReglaNegocioException(
                    "Los años de experiencia deben estar entre 0 y " + MAX_ANIOS_EXPERIENCIA + ".");
        }

        // regla 4: la relación "trabaja en" tiene multiplicidad 1..*
        List<String> prestadoresNormalizados = normalizarPrestadores(prestadores);
        if (prestadoresNormalizados.isEmpty()) {
            throw new ReglaNegocioException(
                    "Hay que indicar al menos un prestador de salud en el que trabaja.");
        }

        TrabajadorSalud nuevo = new TrabajadorSalud(numeroRegistroMSP.trim(), nombreCompleto.trim(),
                especialidad.trim(), fechaAlta, aniosExperiencia, prestadoresNormalizados);

        // regla 1: el número de registro MSP no puede repetirse
        TrabajadorSalud creado = dao.crearSiNoExisteRegistro(nuevo);
        if (creado == null) {
            throw new ReglaNegocioException(
                    "Ya existe un trabajador con el registro MSP " + numeroRegistroMSP.trim() + ".");
        }
        return creado;
    }

    @Override
    public List<TrabajadorSalud> obtenerTrabajadores() {
        LOG.info("obtenerTrabajadores() atendido por la instancia " + System.identityHashCode(this));
        return dao.listar();
    }

    @Override
    public List<TrabajadorSalud> buscarPorEspecialidad(String especialidad) {
        LOG.info("buscarPorEspecialidad() atendido por la instancia " + System.identityHashCode(this));
        return dao.buscarPorEspecialidad(especialidad);
    }

    private static boolean esVacio(String valor) {
        return valor == null || valor.isBlank();
    }

    // saca vacíos y repetidos, conservando el orden de ingreso
    private static List<String> normalizarPrestadores(List<String> prestadores) {
        
    	List<String> resultado = new ArrayList<>();
        
        if (prestadores == null) {
            return resultado;
        }
        
        for (String prestador : prestadores) {
            if (prestador == null) {
                continue;
            }
            String limpio = prestador.trim();
            if (!limpio.isEmpty() && !resultado.contains(limpio)) {
                resultado.add(limpio);
            }
        }
        
        return resultado;
    }
}
