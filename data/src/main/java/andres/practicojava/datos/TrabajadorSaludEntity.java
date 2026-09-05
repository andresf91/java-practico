package andres.practicojava.datos;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import andres.practicojava.modelo.TrabajadorSalud;

// mapeo JPA del trabajador de la salud
@Entity
@Table(name = "trabajador_salud")
@NamedQuery(name = "TrabajadorSaludEntity.listar",
            query = "SELECT t FROM TrabajadorSaludEntity t ORDER BY t.id")
@NamedQuery(name = "TrabajadorSaludEntity.porRegistroMSP",
            query = "SELECT t FROM TrabajadorSaludEntity t WHERE LOWER(t.numeroRegistroMSP) = :registro")
@NamedQuery(name = "TrabajadorSaludEntity.porEspecialidad",
            query = "SELECT t FROM TrabajadorSaludEntity t WHERE LOWER(t.especialidad) LIKE :patron ORDER BY t.id")
public class TrabajadorSaludEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // único
    @Column(name = "registro_msp", nullable = false, unique = true, length = 40)
    private String numeroRegistroMSP;

    @Column(name = "nombre_completo", nullable = false, length = 120)
    private String nombreCompleto;

    @Column(nullable = false, length = 80)
    private String especialidad;

    @Column(name = "fecha_alta", nullable = false)
    private LocalDate fechaAlta;

    @Column(name = "anios_experiencia", nullable = false)
    private int aniosExperiencia;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "trabajador_prestador",
                     joinColumns = @JoinColumn(name = "trabajador_id"))
    @Column(name = "rut_prestador", length = 20)
    private List<String> prestadores = new ArrayList<>();

    public TrabajadorSaludEntity() {
    }

    
    public static TrabajadorSaludEntity desde(TrabajadorSalud trabajador) {
        TrabajadorSaludEntity entidad = new TrabajadorSaludEntity();
        entidad.numeroRegistroMSP = trabajador.getNumeroRegistroMSP();
        entidad.nombreCompleto = trabajador.getNombreCompleto();
        entidad.especialidad = trabajador.getEspecialidad();
        entidad.fechaAlta = trabajador.getFechaAlta();
        entidad.aniosExperiencia = trabajador.getAniosExperiencia();
        entidad.prestadores = new ArrayList<>(trabajador.getPrestadores());
        return entidad;
    }

    // arma el objeto que viaja a las otras capas, una suerte de DTO
    public TrabajadorSalud aModelo() {
        TrabajadorSalud trabajador = new TrabajadorSalud(numeroRegistroMSP, nombreCompleto,
                especialidad, fechaAlta, aniosExperiencia, new ArrayList<>(prestadores));
        trabajador.setId(id);
        return trabajador;
    }
}
