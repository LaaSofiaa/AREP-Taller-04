package edu.escuelaing.arem.ASE.app.framework.controllers;

import edu.escuelaing.arem.ASE.app.framework.annotations.GetMapping;
import edu.escuelaing.arem.ASE.app.framework.annotations.RequestMapping;
import edu.escuelaing.arem.ASE.app.framework.annotations.RequestParam;
import edu.escuelaing.arem.ASE.app.framework.annotations.RestController;

/**
 * Controlador para operaciones matemáticas.
 * Define varios endpoints:
 * - `/App/e`: Devuelve el número de Euler (e).
 * - `/App/pi`: Devuelve el valor de Pi.
 * - `/App/suma`: Suma dos números proporcionados como parámetros.
 * - `/App/resta`: Resta dos números proporcionados como parámetros.
 */
@RestController
@RequestMapping("/App")
public class MathController {

    @GetMapping("/e")
    public static String e(String nousada){
        return Double.toString(Math.E);
    }

    @GetMapping("/pi")
    public String pi() {
        return "El valor de Pi es: " + Math.PI;
    }
    @GetMapping("/suma")
    public String suma(@RequestParam("a") String a, @RequestParam("b") String b) {
        double result = Double.parseDouble(a) + Double.parseDouble(b);
        return "Suma = " + result;
    }

    @GetMapping("/resta")
    public String resta(@RequestParam("a") String a, @RequestParam("b") String b) {
        double result = Double.parseDouble(a) - Double.parseDouble(b);
        return "Resta = " + result;
    }




}
