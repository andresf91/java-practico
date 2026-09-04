package andres.practicojava.web.servicios;

import jakarta.ejb.EJB;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebResult;
import jakarta.jws.WebService;

import java.util.List;

import andres.practicojava.modelo.TrabajadorSalud;
import andres.practicojava.negocio.GestorTrabajadoresLocal;
import andres.practicojava.negocio.ReglaNegocioException;

// capa de servicios SOAP
// los nombres públicos se fijan por anotación para no atar el WSDL al código
@WebService(name = "GestorTrabajadores",
            serviceName = "GestorTrabajadoresService",
            portName = "GestorTrabajadoresPort",
            targetNamespace = "http://servicios.web.practicojava.andres/")
public class GestorTrabajadoresWS {

    @EJB
    private GestorTrabajadoresLocal gestor;

    @WebMethod(operationName = "listarTrabajadores")
    @WebResult(name = "trabajador")
    public List<TrabajadorDTO> listar() {
        return TrabajadorDTO.desde(gestor.obtenerTrabajadores());
    }

    @WebMethod(operationName = "buscarPorEspecialidad")
    @WebResult(name = "trabajador")
    public List<TrabajadorDTO> buscarPorEspecialidad(
            @WebParam(name = "especialidad") String especialidad) {
        return TrabajadorDTO.desde(gestor.buscarPorEspecialidad(especialidad));
    }

    @WebMethod(operationName = "obtenerPorRegistroMSP")
    @WebResult(name = "trabajador")
    public TrabajadorDTO obtenerPorRegistroMSP(
            @WebParam(name = "numeroRegistroMSP") String numeroRegistroMSP) {

        TrabajadorSalud trabajador = gestor.buscarPorRegistroMSP(numeroRegistroMSP);
        return trabajador == null ? null : TrabajadorDTO.desde(trabajador);
    }

    // las reglas de negocio incumplidas viajan como SOAP Fault
    @WebMethod(operationName = "agregarTrabajador")
    @WebResult(name = "trabajador")
    public TrabajadorDTO agregar(@WebParam(name = "trabajador") TrabajadorDTO dto)
            throws ReglaNegocioException {

        if (dto == null) {
            throw new ReglaNegocioException("No llegaron los datos del trabajador.");
        }

        TrabajadorSalud creado = gestor.agregar(dto.getNumeroRegistroMSP(), dto.getNombreCompleto(),
                dto.getEspecialidad(), dto.fechaAltaComoFecha(), dto.getAniosExperiencia(),
                dto.getPrestadores());

        return TrabajadorDTO.desde(creado);
    }
}
