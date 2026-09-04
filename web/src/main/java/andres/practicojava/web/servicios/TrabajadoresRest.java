package andres.practicojava.web.servicios;

import jakarta.ejb.EJB;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import java.util.List;

import andres.practicojava.mensajeria.EmisorAltaLocal;
import andres.practicojava.modelo.TrabajadorSalud;
import andres.practicojava.negocio.GestorTrabajadoresLocal;
import andres.practicojava.negocio.ReglaNegocioException;

// capa de servicios REST

@Path("trabajadores")
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
@Consumes(MediaType.APPLICATION_JSON)
public class TrabajadoresRest {

    @EJB
    private GestorTrabajadoresLocal gestor;

    @EJB
    private EmisorAltaLocal emisor;

    // lista completa o filtrada cuando llega el parámetro de consulta
    @GET
    public List<TrabajadorDTO> listar(@QueryParam("especialidad") String especialidad) {

        List<TrabajadorSalud> trabajadores = (especialidad == null || especialidad.isBlank())
                ? gestor.obtenerTrabajadores()
                : gestor.buscarPorEspecialidad(especialidad);

        return TrabajadorDTO.desde(trabajadores);
    }

    @GET
    @Path("{numeroRegistroMSP}")
    public Response obtener(@PathParam("numeroRegistroMSP") String numeroRegistroMSP) {

        TrabajadorSalud trabajador = gestor.buscarPorRegistroMSP(numeroRegistroMSP);

        if (trabajador == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new MensajeDTO("No existe un trabajador con el registro MSP "
                            + numeroRegistroMSP + "."))
                    .build();
        }
        return Response.ok(TrabajadorDTO.desde(trabajador)).build();
    }

    // alta sincrónica, responde 201 con la ubicación del recurso creado
    @POST
    public Response agregar(TrabajadorDTO dto, @Context UriInfo uriInfo)
            throws ReglaNegocioException {

        if (dto == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new MensajeDTO("El cuerpo del pedido viene vacío."))
                    .build();
        }

        TrabajadorSalud creado = gestor.agregar(dto.getNumeroRegistroMSP(), dto.getNombreCompleto(),
                dto.getEspecialidad(), dto.fechaAltaComoFecha(), dto.getAniosExperiencia(),
                dto.getPrestadores());

        return Response.created(uriInfo.getAbsolutePathBuilder()
                        .path(creado.getNumeroRegistroMSP()).build())
                .entity(TrabajadorDTO.desde(creado))
                .build();
    }

    // alta asincrónica, el mensaje va a la cola y lo consume el MDB
    @POST
    @Path("encolar")
    public Response encolar(TrabajadorDTO dto) {

        if (dto == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new MensajeDTO("El cuerpo del pedido viene vacío."))
                    .build();
        }

        emisor.encolarAlta(dto.getNumeroRegistroMSP(), dto.getNombreCompleto(), dto.getEspecialidad(),
                dto.fechaAltaComoFecha(), dto.getAniosExperiencia(), dto.getPrestadores());

        return Response.accepted()
                .entity(new MensajeDTO("Alta encolada para el registro MSP "
                        + dto.getNumeroRegistroMSP() + "."))
                .build();
    }
}
