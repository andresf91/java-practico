package andres.practicojava.negocio;

import jakarta.ejb.Local;

// interfaz local: la usa el servlet
@Local
public interface GestorTrabajadoresLocal extends GestorTrabajadores {
}
