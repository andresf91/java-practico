package andres.practicojava.negocio;

import jakarta.ejb.Remote;

// interfaz remota: la usa el cliente de consola
@Remote
public interface GestorTrabajadoresRemote extends GestorTrabajadores {
}
