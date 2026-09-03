package andres.practicojava.negocio;

import jakarta.ejb.ApplicationException;

// violación de una regla de negocio, se detecta antes de escribir nada
// cambio para que no genere rollback ante una excepción
@ApplicationException(rollback = false)
public class ReglaNegocioException extends Exception {

    private static final long serialVersionUID = 1L;

    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }
}
