package andres.practicojava.datos;

import jakarta.ejb.Local;

// interfaz local para los componentes que corren en la misma JVM
@Local
public interface TrabajadorDaoLocal extends TrabajadorDao {
}
