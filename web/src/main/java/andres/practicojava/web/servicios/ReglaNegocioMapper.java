package andres.practicojava.web.servicios;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import andres.practicojava.negocio.RegistroDuplicadoException;
import andres.practicojava.negocio.ReglaNegocioException;

// traduce las reglas de negocio a códigos de estado HTTP
@Provider
public class ReglaNegocioMapper implements ExceptionMapper<ReglaNegocioException> {

    @Override
    public Response toResponse(ReglaNegocioException excepcion) {

        Response.Status estado = (excepcion instanceof RegistroDuplicadoException)
                ? Response.Status.CONFLICT
                : Response.Status.BAD_REQUEST;

        return Response.status(estado)
                .entity(new MensajeDTO(excepcion.getMessage()))
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
