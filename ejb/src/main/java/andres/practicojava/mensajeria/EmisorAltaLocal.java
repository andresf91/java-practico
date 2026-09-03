package andres.practicojava.mensajeria;

import jakarta.ejb.Local;

import java.time.LocalDate;
import java.util.List;

// productor de mensajes de alta, lo usan las capas de presentación que corren en el contenedor
@Local
public interface EmisorAltaLocal {

    void encolarAlta(String numeroRegistroMSP, String nombreCompleto, String especialidad,
                     LocalDate fechaAlta, int aniosExperiencia, List<String> prestadores);
}
