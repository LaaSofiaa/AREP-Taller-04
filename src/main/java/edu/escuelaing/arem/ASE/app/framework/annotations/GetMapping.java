package edu.escuelaing.arem.ASE.app.framework.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * Anotación para mapear peticiones HTTP GET a métodos específicos en un controlador.
 * Se usa en métodos de una clase anotada con @RestController.
 *
 * Ejemplo de uso:
 * <pre>
 * {@code
 * @RestController
 * public class ExampleController {
 *     @GetMapping("/hello")
 *     public String hello() {
 *         return "Hola Mundo!";
 *     }}}
 * </pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)//para los metodos
public @interface GetMapping {
    String value();
}
