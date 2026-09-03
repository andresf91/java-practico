package andres.practicojava.datos;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.ejb.AccessTimeout;
import jakarta.ejb.ConcurrencyManagement;
import jakarta.ejb.ConcurrencyManagementType;
import jakarta.ejb.Lock;
import jakarta.ejb.LockType;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import andres.practicojava.modelo.TrabajadorSalud;

@Singleton
@Startup
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
@AccessTimeout(value = 10, unit = TimeUnit.SECONDS)
public class TrabajadorDaoBean implements TrabajadorDaoLocal, TrabajadorDaoRemote {

    private static final Logger LOG = Logger.getLogger(TrabajadorDaoBean.class.getName());

    private final Map<Long, TrabajadorSalud> almacen = new LinkedHashMap<>();
    private final AtomicLong secuencia = new AtomicLong(0);

    @PostConstruct
    public void inicializar() {
        LOG.info("### Singleton TrabajadorDaoBean creado, instancia " + System.identityHashCode(this));

        crear(new TrabajadorSalud("MSP-1001", "Ana Pereira", "Cardiología",
                LocalDate.of(2019, 3, 12), 12, List.of("214771230011")));
        crear(new TrabajadorSalud("MSP-1002", "Bruno Silva", "Pediatría",
                LocalDate.of(2021, 7, 1), 6, List.of("214771230011", "215558820013")));
        crear(new TrabajadorSalud("MSP-1003", "Carla Núñez", "Cardiología",
                LocalDate.of(2023, 11, 20), 3, List.of("215558820013")));
    }

    @PreDestroy
    public void cerrar() {
        LOG.info("### Singleton TrabajadorDaoBean destruido, instancia " + System.identityHashCode(this));
    }
    
    //crear

    @Override
    @Lock(LockType.WRITE)
    public TrabajadorSalud crear(TrabajadorSalud trabajador) {
        trabajador.setId(secuencia.incrementAndGet());
        almacen.put(trabajador.getId(), trabajador);
        LOG.info("Alta en capa de datos: " + trabajador);
        return trabajador;
    }
    
    @Override
    @Lock(LockType.WRITE)
    public TrabajadorSalud crearSiNoExisteRegistro(TrabajadorSalud trabajador) {
        // consulta y alta en la misma sección crítica
        if (buscarPorRegistroMSP(trabajador.getNumeroRegistroMSP()) != null) {
            return null;
        }
        return crear(trabajador);
    }

    //listar

    @Override
    @Lock(LockType.READ)
    public List<TrabajadorSalud> listar() {
        return new ArrayList<>(almacen.values());
    }
    
    //buscadores

    @Override
    @Lock(LockType.READ)
    public TrabajadorSalud buscarPorRegistroMSP(String numeroRegistroMSP) {
        if (numeroRegistroMSP == null) {
            return null;
        }
        return almacen.values().stream()
                .filter(t -> numeroRegistroMSP.trim().equalsIgnoreCase(t.getNumeroRegistroMSP()))
                .findFirst()
                .orElse(null);
    }

    @Override
    @Lock(LockType.READ)
    public List<TrabajadorSalud> buscarPorEspecialidad(String especialidad) {
        if (especialidad == null || especialidad.isBlank()) {
            return listar();
        }
        String patron = especialidad.trim().toLowerCase();
        return almacen.values().stream()
                .filter(t -> t.getEspecialidad() != null
                        && t.getEspecialidad().toLowerCase().contains(patron))
                .collect(Collectors.toCollection(ArrayList::new));
    }
}
