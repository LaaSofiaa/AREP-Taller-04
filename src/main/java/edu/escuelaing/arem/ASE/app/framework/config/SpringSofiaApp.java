package edu.escuelaing.arem.ASE.app.framework.config;

import edu.escuelaing.arem.ASE.app.framework.annotations.SpringSofiaScan;

/**
 * Clase de configuración principal del framework.
 * La anotación @SpringSofiaScan permite definir el paquete donde se buscarán
 * las clases controladoras que manejarán las solicitudes HTTP.
 * En este caso, el framework buscará en el paquete:
 * "edu.escuelaing.arem.ASE.app.framework.controllers"
 */
@SpringSofiaScan("edu.escuelaing.arem.ASE.app.framework.controllers")
public class SpringSofiaApp {
}
