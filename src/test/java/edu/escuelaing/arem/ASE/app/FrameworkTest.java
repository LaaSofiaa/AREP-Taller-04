package edu.escuelaing.arem.ASE.app;

import edu.escuelaing.arem.ASE.app.framework.http.HttpRequest;
import edu.escuelaing.arem.ASE.app.framework.http.HttpResponse;
import edu.escuelaing.arem.ASE.app.framework.http.HttpServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.PrintWriter;

import static org.junit.jupiter.api.Assertions.*;

public class FrameworkTest {
    @BeforeEach
    public void setUp() throws Exception {
        HttpServer.getDataStore().clear();
        HttpServer.getServices().clear();
        HttpServer.loadComponents(); // Carga los controladores
        App.staticfiles("src/main/java/resources");
    }

    @Test
    public void testGreeting() {
        HttpRequest httpRequest = new HttpRequest("/App/greeting?name=Sofia");
        HttpResponse httpResponse = new HttpResponse(new PrintWriter(System.out));
        var handler = HttpServer.getServices().get("/App/greeting");
        assertNotNull(handler, "El manejador para /App/greeting no está registrado.");
        String result = handler.apply(httpRequest, httpResponse);
        assertEquals("Hola, Sofia!", result);
    }

    @Test
    public void testGreetings() {
        HttpRequest httpRequest = new HttpRequest("/App/greetings?name=Sofia&age=22");
        HttpResponse httpResponse = new HttpResponse(new PrintWriter(System.out));
        var handler = HttpServer.getServices().get("/App/greetings");
        assertNotNull(handler, "El manejador para /App/greetings no está registrado.");
        String result = handler.apply(httpRequest, httpResponse);
        assertEquals("Hola, Sofia! Tienes 22 años.", result);
    }

    @Test
    public void testEuler() {
        HttpRequest httpRequest = new HttpRequest("/App/e");
        HttpResponse httpResponse = new HttpResponse(new PrintWriter(System.out));
        var handler = HttpServer.getServices().get("/App/e");
        assertNotNull(handler, "El manejador para /App/e no está registrado.");
        String result = handler.apply(httpRequest, httpResponse);
        assertEquals("2.718281828459045", result);
    }

    @Test
    public void testPi() {
        HttpRequest httpRequest = new HttpRequest("/App/pi");
        HttpResponse httpResponse = new HttpResponse(new PrintWriter(System.out));
        var handler = HttpServer.getServices().get("/App/pi");
        assertNotNull(handler, "El manejador para /App/pi no está registrado.");
        String result = handler.apply(httpRequest, httpResponse);
        assertEquals("El valor de Pi es: 3.141592653589793", result);
    }

    @Test
    public void testSuma() {
        HttpRequest httpRequest = new HttpRequest("/App/suma?a=5&b=3");
        HttpResponse httpResponse = new HttpResponse(new PrintWriter(System.out));
        var handler = HttpServer.getServices().get("/App/suma");
        assertNotNull(handler, "El manejador para /App/suma no está registrado.");
        String result = handler.apply(httpRequest, httpResponse);
        assertEquals("Suma = 8.0", result);
    }

    @Test
    public void testResta() {
        HttpRequest httpRequest = new HttpRequest("/App/resta?a=10&b=4");
        HttpResponse httpResponse = new HttpResponse(new PrintWriter(System.out));
        var handler = HttpServer.getServices().get("/App/resta");
        assertNotNull(handler, "El manejador para /App/resta no está registrado.");
        String result = handler.apply(httpRequest, httpResponse);
        assertEquals("Resta = 6.0", result);
    }

    @Test
    public void testError404() {
        HttpRequest httpRequest = new HttpRequest("/App/noExiste");
        HttpResponse httpResponse = new HttpResponse(new PrintWriter(System.out));
        var handler = HttpServer.getServices().get("/App/noExiste");
        assertNull(handler, "No debería existir un manejador para /App/noExiste.");
    }
}
