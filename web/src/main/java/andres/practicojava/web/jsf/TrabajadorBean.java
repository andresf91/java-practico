package andres.practicojava.web.jsf;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import andres.practicojava.mensajeria.EmisorAltaLocal;
import andres.practicojava.modelo.TrabajadorSalud;
import andres.practicojava.negocio.GestorTrabajadoresLocal;
import andres.practicojava.negocio.ReglaNegocioException;

// backing bean de la vista JSF, convierte y delega en la capa de negocio
@Named
@ViewScoped
public class TrabajadorBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @EJB
    private GestorTrabajadoresLocal gestor;

    @EJB
    private EmisorAltaLocal emisor;

    // formulario de alta
    private String numeroRegistroMSP;
    private String nombreCompleto;
    private String especialidad;
    private LocalDate fechaAlta;
    private int aniosExperiencia;
    private List<String> prestadores = new ArrayList<>();

    // listado
    private String filtroEspecialidad;
    private List<TrabajadorSalud> trabajadores = new ArrayList<>();

    @PostConstruct
    public void inicializar() {
        buscar();
    }

    public void agregar() {
        try {
            TrabajadorSalud alta = gestor.agregar(numeroRegistroMSP, nombreCompleto, especialidad,
                    fechaAlta, aniosExperiencia, prestadores);
            avisar(FacesMessage.SEVERITY_INFO, "Trabajador agregado",
                    alta.getNombreCompleto() + ", registro " + alta.getNumeroRegistroMSP());
            limpiarFormulario();
        } catch (ReglaNegocioException e) {
            
            avisar(FacesMessage.SEVERITY_ERROR, "No se pudo agregar", e.getMessage());
        }
        buscar();
    }

    // se manda el alta a la cola
    public void encolarAlta() {
        emisor.encolarAlta(numeroRegistroMSP, nombreCompleto, especialidad,
                fechaAlta, aniosExperiencia, prestadores);
        avisar(FacesMessage.SEVERITY_INFO, "Solicitud encolada",
                "El alta de " + nombreCompleto + " se procesa de forma asincrónica.");
        limpiarFormulario();
    }

    // el gestor devuelve todos cuando el filtro viene vacío
    public void buscar() {
        trabajadores = gestor.buscarPorEspecialidad(filtroEspecialidad);
    }

    public void verTodos() {
        filtroEspecialidad = null;
        buscar();
    }

    // tope del calendario, la fecha de alta no puede ser futura
    public LocalDate getHoy() {
        return LocalDate.now();
    }

    private void limpiarFormulario() {
        numeroRegistroMSP = null;
        nombreCompleto = null;
        especialidad = null;
        fechaAlta = null;
        aniosExperiencia = 0;
        prestadores = new ArrayList<>();
    }

    private void avisar(FacesMessage.Severity severidad, String resumen, String detalle) {
        // mensaje se muestra por p:growl
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severidad, resumen, detalle));
    }

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
    public void setPrestadores(List<String> prestadores) { this.prestadores = prestadores; }

    public String getFiltroEspecialidad() { return filtroEspecialidad; }
    public void setFiltroEspecialidad(String filtroEspecialidad) { this.filtroEspecialidad = filtroEspecialidad; }

    public List<TrabajadorSalud> getTrabajadores() { return trabajadores; }
}
