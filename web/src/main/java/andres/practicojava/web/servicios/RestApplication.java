package andres.practicojava.web.servicios;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

// activa JAX-RS y fija el path base de los recursos
@ApplicationPath("/rest")
public class RestApplication extends Application {
}
