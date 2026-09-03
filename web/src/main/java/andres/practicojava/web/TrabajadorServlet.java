package andres.practicojava.web;

import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;

import andres.practicojava.modelo.TrabajadorSalud;
import andres.practicojava.negocio.GestorTrabajadoresLocal;
import andres.practicojava.negocio.ReglaNegocioException;

// capa de presentación web
@WebServlet(name = "TrabajadorServlet", urlPatterns = {"/trabajadores"})
public class TrabajadorServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final String VISTA = "/WEB-INF/jsp/trabajadores.jsp";

    // interfaz local, el servlet y el EJB corren en el mismo WildFly
    @EJB
    private GestorTrabajadoresLocal gestor;

    // listar y buscar
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String especialidad = req.getParameter("especialidad");
        List<TrabajadorSalud> trabajadores;

        if (especialidad != null && !especialidad.isBlank()) {
            trabajadores = gestor.buscarPorEspecialidad(especialidad);
            req.setAttribute("especialidadBuscada", especialidad);
        } else {
            trabajadores = gestor.obtenerTrabajadores();
        }

        req.setAttribute("trabajadores", trabajadores);
        req.getRequestDispatcher(VISTA).forward(req, resp);
    }

    // agregar
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");

        try {
            LocalDate fechaAlta = parsearFecha(req.getParameter("fechaAlta"));
            int anios = parsearEntero(req.getParameter("aniosExperiencia"));
            List<String> prestadores = parsearPrestadores(req.getParameter("prestadores"));

            TrabajadorSalud alta = gestor.agregar(
                    req.getParameter("numeroRegistroMSP"),
                    req.getParameter("nombreCompleto"),
                    req.getParameter("especialidad"),
                    fechaAlta,
                    anios,
                    prestadores);

            req.setAttribute("mensaje", "Trabajador agregado: " + alta);

        } catch (ReglaNegocioException e) {
            req.setAttribute("error", e.getMessage());
        }

        req.setAttribute("trabajadores", gestor.obtenerTrabajadores());
        req.getRequestDispatcher(VISTA).forward(req, resp);
    }

    private LocalDate parsearFecha(String valor) throws ReglaNegocioException {
        if (valor == null || valor.isBlank()) {
            throw new ReglaNegocioException("La fecha de alta es obligatoria.");
        }
        try {
            return LocalDate.parse(valor.trim()); // formato aaaa-MM-dd
        } catch (DateTimeParseException e) {
            throw new ReglaNegocioException("Fecha de alta inválida: " + valor);
        }
    }

    private int parsearEntero(String valor) throws ReglaNegocioException {
        try {
            return Integer.parseInt(valor == null ? "" : valor.trim());
        } catch (NumberFormatException e) {
            throw new ReglaNegocioException("Los años de experiencia deben ser un número entero.");
        }
    }

    // los RUT de los prestadores llegan separados por coma
    private List<String> parsearPrestadores(String valor) {
        if (valor == null || valor.isBlank()) {
            return List.of();
        }
        return Arrays.asList(valor.split(","));
    }
}
