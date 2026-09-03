package andres.practicojava.negocio;

import jakarta.ejb.ApplicationException;

// violación de una regla de negocio
@ApplicationException(rollback = true)
public class ReglaNegocioException extends Exception {

    private static final long serialVersionUID = 1L;

    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }
}
