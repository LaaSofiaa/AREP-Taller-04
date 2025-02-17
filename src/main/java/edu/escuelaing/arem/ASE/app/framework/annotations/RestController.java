package edu.escuelaing.arem.ASE.app.framework.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Anotación para marcar una clase como un controlador REST.
 * Indica que la clase maneja solicitudes HTTP y devuelve respuestas en formato String.
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
@Retention(RetentionPolicy.RUNTIME) //ejecucion
@Target(ElementType.TYPE) // para las clases
public @interface RestController {
}
