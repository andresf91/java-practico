package andres.practicojava.mensajeria;

import jakarta.ejb.ActivationConfigProperty;
import jakarta.ejb.EJB;
import jakarta.ejb.EJBException;
import jakarta.ejb.MessageDriven;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.MessageListener;
import jakarta.jms.TextMessage;

import java.util.logging.Logger;

import andres.practicojava.modelo.TrabajadorSalud;
import andres.practicojava.negocio.GestorTrabajadoresLocal;
import andres.practicojava.negocio.ReglaNegocioException;

// consumidor asincrónico de la cola de altas, varias sesiones atienden en paralelo
@MessageDriven(name = "AltaTrabajadorMDB", activationConfig = {
        @ActivationConfigProperty(propertyName = "destinationType",
                                  propertyValue = "jakarta.jms.Queue"),
        @ActivationConfigProperty(propertyName = "destinationLookup",
                                  propertyValue = MensajeAlta.JNDI_COLA),
        @ActivationConfigProperty(propertyName = "acknowledgeMode",
                                  propertyValue = "Auto-acknowledge"),
        @ActivationConfigProperty(propertyName = "maxSession",
                                  propertyValue = "5")
})
public class AltaTrabajadorMDB implements MessageListener {

    private static final Logger LOG = Logger.getLogger(AltaTrabajadorMDB.class.getName());

    @EJB
    private GestorTrabajadoresLocal gestor;

    @Override
    public void onMessage(Message mensaje) {

        LOG.info("onMessage() atendido por la instancia " + System.identityHashCode(this));

        if (!(mensaje instanceof TextMessage texto)) {
            LOG.warning("Se descarta un mensaje que no es de texto: " + mensaje.getClass().getName());
            return;
        }

        String contenido;
        try {
            contenido = texto.getText();
        } catch (JMSException e) {
            // si hay JMSException, propagar para que el contenedor reintente
            throw new EJBException("No se pudo leer el cuerpo del mensaje.", e);
        }

        try {
            MensajeAlta alta = MensajeAlta.parsear(contenido);

            TrabajadorSalud creado = gestor.agregar(alta.numeroRegistroMSP(), alta.nombreCompleto(),
                    alta.especialidad(), alta.fechaAlta(), alta.aniosExperiencia(), alta.prestadores());

            LOG.info("Alta asincrónica realizada: " + creado);

        } catch (IllegalArgumentException e) {
            // mensaje mal formado, reintentarlo daría siempre el mismo resultado
            LOG.warning("Mensaje descartado por formato: " + e.getMessage()
                    + " Contenido: " + contenido);

        } catch (ReglaNegocioException e) {
            LOG.warning("Alta rechazada por regla de negocio: " + e.getMessage()
                    + " Contenido: " + contenido);
        }
    }
}
