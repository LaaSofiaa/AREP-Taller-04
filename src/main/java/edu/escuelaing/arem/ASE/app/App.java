package edu.escuelaing.arem.ASE.app;

import edu.escuelaing.arem.ASE.app.framework.http.HttpServer;
import static edu.escuelaing.arem.ASE.app.framework.http.HttpServer.get;

public class App {
    private static String staticFilesDirectory = "src/main/java/resources";

    /**
     * Método principal que inicia el servidor y configura servicios.
     */
    public static void main(String[] args) throws Exception {
        staticfiles("src/main/java/resources");
        HttpServer.loadComponents();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Apagando el servidor...");
            HttpServer.stopServer();
        }));

        get("/App/hello", (request, respond) -> {
            String name = request.getValues("name");
            if (name == null || name.isEmpty()) {
                name = "usuario";
            }
            //return "{\"name\": \"" + name + "\"}";
            return "Hola " + name ;
        });

        get("/App/pi", (req, resp) -> String.valueOf(Math.PI));

        get("/App/euler", (req, resp) -> String.valueOf(Math.E));
        get("/App/mundo",(req,resp)-> "Hola Mundo");

        HttpServer.startServer();

    }

    /**
     * Define el directorio donde se encuentran los archivos estáticos.
     * @param directory El directorio de archivos estáticos.
     */
    public static void staticfiles(String directory) {
        if (System.getenv("DOCKER_ENV") != null) {
            staticFilesDirectory = "/usrapp/bin/resources"; // Ruta Docker
            System.out.println("Usando ruta Docker: " + staticFilesDirectory);
        } else {
            staticFilesDirectory = directory; // Ruta en local
            System.out.println("Usando ruta local: " + staticFilesDirectory);
        }
    }

    public static String getStaticFilesDirectory() {
        return staticFilesDirectory;
    }

}