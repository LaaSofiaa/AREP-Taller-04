package edu.escuelaing.arem.ASE.app.framework.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Anotación para definir el paquete base donde se buscarán los controladores.
 * Se usa en la clase principal de la aplicación para indicar dónde escanear.
 *
 * Ejemplo de uso:
 * <pre>
 * {@code
 * @SpringSofiaScan("edu.escuelaing.arem.ASE.app.controllers")
 * public class SpringSofiaApp {
 *     public static void main(String[] args) {
 *         SpringSofiaApp.run(SpringSofiaApp.class, args);
 *     }}}
 * </pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface SpringSofiaScan {
    String value();
}
