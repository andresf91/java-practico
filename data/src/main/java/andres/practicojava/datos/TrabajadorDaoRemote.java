package andres.practicojava.datos;

import jakarta.ejb.Remote;

// interfaz remota para los clientes que corren fuera del contenedor
@Remote
public interface TrabajadorDaoRemote extends TrabajadorDao {
}
