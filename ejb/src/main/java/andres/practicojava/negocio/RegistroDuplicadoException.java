package andres.practicojava.negocio;

import jakarta.ejb.ApplicationException;

// el número de registro MSP ya está tomado
@ApplicationException(rollback = false)
public class RegistroDuplicadoException extends ReglaNegocioException {

    private static final long serialVersionUID = 1L;

    public RegistroDuplicadoException(String mensaje) {
        super(mensaje);
    }
}
