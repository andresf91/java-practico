package andres.practicojava.datos;

import jakarta.inject.Qualifier;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

// distingue la implementación de datos con JPA sobre PostgreSQL
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
public @interface ConJpa {
}
