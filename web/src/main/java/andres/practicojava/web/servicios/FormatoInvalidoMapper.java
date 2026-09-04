package andres.practicojava.web.servicios;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

// datos mal formados en el cuerpo del pedido
@Provider
public class FormatoInvalidoMapper implements ExceptionMapper<IllegalArgumentException> {

    @Override
    public Response toResponse(IllegalArgumentException excepcion) {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(new MensajeDTO(excepcion.getMessage()))
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
