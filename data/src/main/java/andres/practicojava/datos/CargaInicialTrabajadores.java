package andres.practicojava.datos;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.inject.Inject;

import java.time.LocalDate;
import java.util.List;
import java.util.logging.Logger;

import andres.practicojava.modelo.TrabajadorSalud;

// datos de ejemplo
@Singleton
@Startup
public class CargaInicialTrabajadores {

    private static final Logger LOG = Logger.getLogger(CargaInicialTrabajadores.class.getName());

    @Inject
    @ConJpa
    private TrabajadorDaoLocal dao;

    @PostConstruct
    public void cargar() {
        if (!dao.listar().isEmpty()) {
            LOG.info("La base ya tiene trabajadores, no se cargan los de ejemplo.");
            return;
        }

        dao.crear(new TrabajadorSalud("MSP-1001", "Ana Pereira", "Cardiología",
                LocalDate.of(2019, 3, 12), 12, List.of("214771230011")));
        dao.crear(new TrabajadorSalud("MSP-1002", "Bruno Silva", "Pediatría",
                LocalDate.of(2021, 7, 1), 6, List.of("214771230011", "215558820013")));
        dao.crear(new TrabajadorSalud("MSP-1003", "Carla Núñez", "Cardiología",
                LocalDate.of(2023, 11, 20), 3, List.of("215558820013")));

        LOG.info("Carga inicial de trabajadores realizada.");
    }
}
