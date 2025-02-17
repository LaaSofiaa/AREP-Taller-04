package edu.escuelaing.arem.ASE.app.framework.controllers;

import edu.escuelaing.arem.ASE.app.framework.annotations.GetMapping;
import edu.escuelaing.arem.ASE.app.framework.annotations.RequestMapping;
import edu.escuelaing.arem.ASE.app.framework.annotations.RequestParam;
import edu.escuelaing.arem.ASE.app.framework.annotations.RestController;

/**
 * Controlador de saludos que maneja solicitudes HTTP GET.
 * Define dos endpoints:
 * - `/App/greeting`: Devuelve un saludo con el nombre proporcionado.
 * - `/App/greetings`: Devuelve un saludo con el nombre y la edad proporcionados.
 */
@RestController
@RequestMapping("/App")
public class GreetingController {
    @GetMapping("/greeting")
    public String greeting(@RequestParam(value = "name", defaultValue = "Usuario") String name) {
        return "Hola, " + name + "!";
    }

    @GetMapping("/greetings")
    public String greeting(
            @RequestParam(value = "name", defaultValue = "Usuario") String name,
            @RequestParam(value = "age", defaultValue = "0") String age
    ) {
        return "Hola, " + name + "! Tienes " + age + " años.";
    }

}
