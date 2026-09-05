package andres.practicojava.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** trabajador de la salud, entidad del modelo de entidades principales de Salud.uy */
@Entity
@Table(name = "trabajador_salud")
@NamedQuery(name = "TrabajadorSalud.listar",
            query = "SELECT t FROM TrabajadorSalud t ORDER BY t.id")
@NamedQuery(name = "TrabajadorSalud.porRegistroMSP",
            query = "SELECT t FROM TrabajadorSalud t WHERE LOWER(t.numeroRegistroMSP) = :registro")
@NamedQuery(name = "TrabajadorSalud.porEspecialidad",
            query = "SELECT t FROM TrabajadorSalud t WHERE LOWER(t.especialidad) LIKE :patron ORDER BY t.id")
public class TrabajadorSalud implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // único, la restricción también vive en la base
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

    /** prestadores de salud en los que trabaja (RUT) */
    // EAGER porque la entidad se serializa fuera del contexto de persistencia
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "trabajador_prestador",
                     joinColumns = @JoinColumn(name = "trabajador_id"))
    @Column(name = "rut_prestador", length = 20)
    private List<String> prestadores = new ArrayList<>();

    public TrabajadorSalud() {
    }

    public TrabajadorSalud(String numeroRegistroMSP, String nombreCompleto, String especialidad,
                           LocalDate fechaAlta, int aniosExperiencia, List<String> prestadores) {
        this.numeroRegistroMSP = numeroRegistroMSP;
        this.nombreCompleto = nombreCompleto;
        this.especialidad = especialidad;
        this.fechaAlta = fechaAlta;
        this.aniosExperiencia = aniosExperiencia;
        setPrestadores(prestadores);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNumeroRegistroMSP() { return numeroRegistroMSP; }
    public void setNumeroRegistroMSP(String numeroRegistroMSP) { this.numeroRegistroMSP = numeroRegistroMSP; }

    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }

    public String getEspecialidad() { return especialidad; }
    public void setEspecialidad(String especialidad) { this.especialidad = especialidad; }

    public LocalDate getFechaAlta() { return fechaAlta; }
    public void setFechaAlta(LocalDate fechaAlta) { this.fechaAlta = fechaAlta; }

    public int getAniosExperiencia() { return aniosExperiencia; }
    public void setAniosExperiencia(int aniosExperiencia) { this.aniosExperiencia = aniosExperiencia; }

    public List<String> getPrestadores() { return prestadores; }

    public void setPrestadores(List<String> prestadores) {
        // ArrayList: tiene que ser serializable y mutable
        this.prestadores = (prestadores == null) ? new ArrayList<>() : new ArrayList<>(prestadores);
    }

    /** los prestadores en una sola línea */
    public String getPrestadoresComoTexto() {
        return String.join(", ", prestadores);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TrabajadorSalud otro)) return false;
        return id != null && id.equals(otro.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return String.format("[%d] %s | reg. MSP %s | %s | alta %s | %d años | prestadores: %s",
                id, nombreCompleto, numeroRegistroMSP, especialidad, fechaAlta, aniosExperiencia,
                getPrestadoresComoTexto());
    }
}
