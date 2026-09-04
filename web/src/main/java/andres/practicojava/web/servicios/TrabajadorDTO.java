package andres.practicojava.web.servicios;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import andres.practicojava.modelo.TrabajadorSalud;

// representación pública de la entidad, la fecha viaja como texto
@XmlRootElement(name = "trabajador")
@XmlAccessorType(XmlAccessType.FIELD)
public class TrabajadorDTO {

    private Long id;
    private String numeroRegistroMSP;
    private String nombreCompleto;
    private String especialidad;
    private String fechaAlta;
    private int aniosExperiencia;
    private List<String> prestadores = new ArrayList<>();

    public TrabajadorDTO() {
    }

    public static TrabajadorDTO desde(TrabajadorSalud trabajador) {
        TrabajadorDTO dto = new TrabajadorDTO();

        dto.id = trabajador.getId();
        dto.numeroRegistroMSP = trabajador.getNumeroRegistroMSP();
        dto.nombreCompleto = trabajador.getNombreCompleto();
        dto.especialidad = trabajador.getEspecialidad();
        dto.fechaAlta = trabajador.getFechaAlta() == null ? null : trabajador.getFechaAlta().toString();
        dto.aniosExperiencia = trabajador.getAniosExperiencia();
        dto.prestadores = new ArrayList<>(trabajador.getPrestadores());

        return dto;
    }

    public static List<TrabajadorDTO> desde(List<TrabajadorSalud> trabajadores) {
        List<TrabajadorDTO> dtos = new ArrayList<>();

        for (TrabajadorSalud trabajador : trabajadores) {
            dtos.add(desde(trabajador));
        }

        return dtos;
    }

    // IllegalArgumentException si el texto no es una fecha ISO
    public LocalDate fechaAltaComoFecha() {
        if (fechaAlta == null || fechaAlta.isBlank()) {
            return null;
        }
        try {
            return LocalDate.parse(fechaAlta.trim());
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Fecha de alta inválida, se espera AAAA-MM-DD, llegó "
                    + fechaAlta);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNumeroRegistroMSP() { return numeroRegistroMSP; }
    public void setNumeroRegistroMSP(String numeroRegistroMSP) { this.numeroRegistroMSP = numeroRegistroMSP; }

    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }

    public String getEspecialidad() { return especialidad; }
    public void setEspecialidad(String especialidad) { this.especialidad = especialidad; }

    public String getFechaAlta() { return fechaAlta; }
    public void setFechaAlta(String fechaAlta) { this.fechaAlta = fechaAlta; }

    public int getAniosExperiencia() { return aniosExperiencia; }
    public void setAniosExperiencia(int aniosExperiencia) { this.aniosExperiencia = aniosExperiencia; }

    public List<String> getPrestadores() { return prestadores; }

    public void setPrestadores(List<String> prestadores) {
        this.prestadores = (prestadores == null) ? new ArrayList<>() : new ArrayList<>(prestadores);
    }
}
