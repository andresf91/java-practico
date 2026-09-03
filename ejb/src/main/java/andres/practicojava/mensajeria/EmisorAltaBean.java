package andres.practicojava.mensajeria;

import jakarta.annotation.Resource;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.jms.JMSContext;
import jakarta.jms.JMSDestinationDefinition;
import jakarta.jms.Queue;

import java.time.LocalDate;
import java.util.List;
import java.util.logging.Logger;

// definición de la cola
@JMSDestinationDefinition(
        name = MensajeAlta.JNDI_COLA,
        interfaceName = "jakarta.jms.Queue",
        destinationName = MensajeAlta.NOMBRE_COLA)
@Stateless
public class EmisorAltaBean implements EmisorAltaLocal {

    private static final Logger LOG = Logger.getLogger(EmisorAltaBean.class.getName());

    @Inject
    private JMSContext contexto;

    @Resource(lookup = MensajeAlta.JNDI_COLA)
    private Queue colaAlta;

    @Override
    public void encolarAlta(String numeroRegistroMSP, String nombreCompleto, String especialidad,
                            LocalDate fechaAlta, int aniosExperiencia, List<String> prestadores) {

        String mensaje = MensajeAlta.armar(numeroRegistroMSP, nombreCompleto, especialidad,
                fechaAlta, aniosExperiencia, prestadores);

        contexto.createProducer().send(colaAlta, mensaje);
        LOG.info("Encolado en " + MensajeAlta.NOMBRE_COLA + ": " + mensaje);
    }
}
