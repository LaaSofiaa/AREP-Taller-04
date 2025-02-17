package edu.escuelaing.arem.ASE.app.framework.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Anotación para indicar que un parámetro de método es un parámetro de consulta HTTP.
 * Se usa en métodos anotados con @GetMapping.
 *
 * Ejemplo de uso:
 * <pre>
 * {@code
 * @RestController
 * public class ExampleController {
 *     @GetMapping("/hello")
 *     public String hello(@RequestParam("name") String name) {
 *         return "Hola, " + name + "!";
 *     }}}
 * </pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)//para pametros de los metodos
public @interface RequestParam {
    String value();
    String defaultValue() default "__NO_DEFAULT__";
}
