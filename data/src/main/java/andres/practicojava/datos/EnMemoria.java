package andres.practicojava.datos;

import jakarta.inject.Qualifier;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

// distingue la implementación de datos en memoria
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
public @interface EnMemoria {
}
