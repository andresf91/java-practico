package andres.practicojava.modelo;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** trabajador de la salud, entidad del modelo de entidades principales de Salud.uy */
public class TrabajadorSalud implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String numeroRegistroMSP;
    private String nombreCompleto;
    private String especialidad;
    private LocalDate fechaAlta;
    private int aniosExperiencia;

    /** prestadores de salud en los que trabaja (RUT) */
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
