package edu.escuelaing.arem.ASE.app.framework.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * Anotación para mapear una clase controladora a una ruta base.
 * Se usa en clases anotadas con @RestController para definir una ruta raíz.
 *
 * Ejemplo de uso:
 * <pre>
 * {@code
 * @RestController
 * @RequestMapping("/api")
 * public class ApiController {
 *     @GetMapping("/hello")
 *     public String hello() {
 *         return "Hola desde /api/hello!";
 *     }}}
 * </pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface RequestMapping {
    String value();
}
