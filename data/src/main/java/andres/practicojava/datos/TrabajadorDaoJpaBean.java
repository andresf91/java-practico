package andres.practicojava.datos;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import andres.practicojava.modelo.TrabajadorSalud;

// capa de datos con JPA, movemos el estado a PostgreSQL
@Stateless
@ConJpa
public class TrabajadorDaoJpaBean implements TrabajadorDaoLocal, TrabajadorDaoRemote {

    private static final Logger LOG = Logger.getLogger(TrabajadorDaoJpaBean.class.getName());

    @PersistenceContext(unitName = "trabajadoresPU")
    private EntityManager em;

    //crear

    @Override
    public TrabajadorSalud crear(TrabajadorSalud trabajador) {
        TrabajadorSaludEntity entidad = TrabajadorSaludEntity.desde(trabajador);
        em.persist(entidad);

        // flush para que el INSERT ocurra ahora y vuelva el id generado
        em.flush();

        TrabajadorSalud creado = entidad.aModelo();
        LOG.info("Alta en capa de datos JPA: " + creado);
        return creado;
    }

    @Override
    public TrabajadorSalud crearSiNoExisteRegistro(TrabajadorSalud trabajador) {

        // consulta y alta en la misma transacción
        if (buscarPorRegistroMSP(trabajador.getNumeroRegistroMSP()) != null) {
            return null;
        }
        return crear(trabajador);
    }

    //listar

    @Override
    public List<TrabajadorSalud> listar() {
        return aModelo(em.createNamedQuery("TrabajadorSaludEntity.listar", TrabajadorSaludEntity.class)
                .getResultList());
    }

    //buscadores

    @Override
    public TrabajadorSalud buscarPorRegistroMSP(String numeroRegistroMSP) {
        if (numeroRegistroMSP == null || numeroRegistroMSP.isBlank()) {
            return null;
        }
        return em.createNamedQuery("TrabajadorSaludEntity.porRegistroMSP", TrabajadorSaludEntity.class)
                .setParameter("registro", numeroRegistroMSP.trim().toLowerCase())
                .getResultStream()
                .findFirst()
                .map(TrabajadorSaludEntity::aModelo)
                .orElse(null);
    }

    @Override
    public List<TrabajadorSalud> buscarPorEspecialidad(String especialidad) {
        if (especialidad == null || especialidad.isBlank()) {
            return listar();
        }

        return aModelo(em.createNamedQuery("TrabajadorSaludEntity.porEspecialidad", TrabajadorSaludEntity.class)
                .setParameter("patron", "%" + especialidad.trim().toLowerCase() + "%")
                .getResultList());
    }

    private static List<TrabajadorSalud> aModelo(List<TrabajadorSaludEntity> entidades) {
        List<TrabajadorSalud> resultado = new ArrayList<>(entidades.size());
        for (TrabajadorSaludEntity entidad : entidades) {
            resultado.add(entidad.aModelo());
        }
        return resultado;
    }
}
