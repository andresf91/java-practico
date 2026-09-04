package andres.practicojava.web.servicios;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

// cuerpo de las respuestas de los servicios REST que no devuelven una entidad
@XmlRootElement(name = "mensaje")
@XmlAccessorType(XmlAccessType.FIELD)
public class MensajeDTO {

    private String mensaje;

    public MensajeDTO() {
    }

    public MensajeDTO(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }
}
