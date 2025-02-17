package edu.escuelaing.arem.ASE.app.framework.http;

import edu.escuelaing.arem.ASE.app.framework.annotations.*;
import edu.escuelaing.arem.ASE.app.framework.config.SpringSofiaApp;

import java.io.*;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.*;
import java.util.function.BiFunction;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Esta clase implementa un servidor HTTP básico que maneja solicitudes GET y POST.
 * Sirve archivos estáticos desde un directorio y proporciona una API sencilla para
 * manejar el nombre del usuario.
 */
public class HttpServer {
    private static final int port = 35000;
    private static String staticFilesDirectory = "src/main/java/resources";
    private static final Map<String, String> dataStore = new HashMap<>();
    private static final Map<String, BiFunction<HttpRequest, HttpResponse, String>> services = new HashMap<>(); // almacenar las rutas y sus manejadores (funciones lambda)


    /**
     * Inicia el servidor y espera conexiones entrantes.
     */
    public static void startServer(){

        /// Crea el servidor que escucha en el puerto
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Servidor escuchando en el puerto: " + port);
            boolean running = true;
            while (running) {
                // Acepta una nueva conexión y maneja la solictud del cliente
                Socket clientSocket = serverSocket.accept();
                handleRequestClient(clientSocket);
            }
        } catch (IOException e) {
            System.err.println("No se pudo escuchar en el puerto: " + port);
            System.exit(1);
        }
    }

    /**
     * Metodo encargado de cargar los componentes de la aplicación, escaneando los
     * controladores anotados con @RestController y registrando sus rutas.
     */
    public static void loadComponents() throws Exception {
        Class<?> configClass = SpringSofiaApp.class;
        if (!configClass.isAnnotationPresent(SpringSofiaScan.class)) {
            throw new RuntimeException("SpringSofiaApp no tiene la anotación @SpringSofiaScan");
        }
        String packageToScan = configClass.getAnnotation(SpringSofiaScan.class).value();
        List<Class<?>> controllers = findControllers( packageToScan);

        for (Class<?> controller : controllers) {
            if (!controller.isAnnotationPresent(RestController.class)) continue;

            String basePath = "";
            if (controller.isAnnotationPresent(RequestMapping.class)) {
                basePath = controller.getAnnotation(RequestMapping.class).value();
            }

            System.out.println("Controlador detectado: " + controller.getName() + " en " + basePath);
            Object instance = controller.getDeclaredConstructor().newInstance();

            //mirar si estan los metodos declarados
            for (Method method : controller.getDeclaredMethods()) {
                String methodPath = basePath;

                if (method.isAnnotationPresent(RequestMapping.class)) {
                    RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
                    methodPath += requestMapping.value();
                }

                if (method.isAnnotationPresent(GetMapping.class)) {
                    GetMapping annotation = method.getAnnotation(GetMapping.class);
                    String path = methodPath + annotation.value();
                    System.out.println("Ruta registrada: " + path);

                    services.put(path, (req, res) -> {
                        try {
                            Parameter[] parameters = method.getParameters();
                            Object[] argsValues = new Object[parameters.length];

                            for (int i = 0; i < parameters.length; i++) {
                                if (parameters[i].isAnnotationPresent(RequestParam.class)) {
                                    RequestParam paramAnnotation = parameters[i].getAnnotation(RequestParam.class);
                                    String paramName = paramAnnotation.value();
                                    String paramValue = req.getValues(paramName);
                                    String defaultValue = paramAnnotation.defaultValue();
                                    argsValues[i] = (paramValue != null && !paramValue.isEmpty()) ? paramValue
                                            : (!defaultValue.equals("__NO_DEFAULT__") ? defaultValue : null);

                                }
                            }

                            return (String) method.invoke(instance, argsValues);
                        } catch (Exception e) {
                            e.printStackTrace();
                            return "Error en el controlador";
                        }
                    });

                    System.out.println("Registrado servicio en: " + path);
                }
            }
        }
    }

    /**
     * Busca las clases dentro del paquete especificado que están anotadas con @RestController.
     * @param packageName Nombre del paquete donde se buscarán los controladores.
     * @return Lista de clases que contienen la anotación @RestController.
     * @throws Exception En caso de error al acceder a las clases.
     */
    private static List<Class<?>> findControllers(String packageName) throws Exception {
        List<Class<?>> classes = new ArrayList<>();
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = Thread.currentThread().getContextClassLoader().getResources(path);

        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            if (resource.getProtocol().equals("file")) {
                File directory = new File(resource.toURI());
                for (String file : directory.list()) {
                    if (file.endsWith(".class")) {
                        String className = packageName + "." + file.replace(".class", "");
                        classes.add(Class.forName(className));
                    }
                }
            } else if (resource.getProtocol().equals("jar")) {
                classes.addAll(findClassesInJar(resource, packageName));
            }
        }

        return classes;
    }

    private static List<Class<?>> findClassesInJar(URL resource, String packageName) throws IOException, ClassNotFoundException {
        List<Class<?>> classes = new ArrayList<>();
        String jarPath = resource.getPath().substring(5, resource.getPath().indexOf("!"));
        try (JarFile jar = new JarFile(jarPath)) {
            Enumeration<JarEntry> entries = jar.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String name = entry.getName();
                if (name.startsWith(packageName.replace('.', '/')) && name.endsWith(".class")) {
                    String className = name.replace("/", ".").replace(".class", "");
                    classes.add(Class.forName(className));
                }
            }
        }
        return classes;
    }

    /**
     * Maneja la solicitud de un cliente y delega la acción según el tipo de solicitud.
     * @param clientSocket El socket de la conexión con el cliente.
     */
    public static void handleRequestClient(Socket clientSocket) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
             BufferedOutputStream dataOut = new BufferedOutputStream(clientSocket.getOutputStream())) {

            String requestLine = in.readLine();
            if (requestLine != null) {
                System.out.println("Solicitud recibida: " + requestLine);
                // Divide la línea de la solicitud en partes: método y recurso.
                String[] tokens = requestLine.split(" ");
                String method = tokens[0];
                String fileRequested = tokens[1];

                if (fileRequested.equals("/")) {
                    fileRequested = "/index.html";
                }

                if (method.equals("GET")) {
                    handleGetRequest(fileRequested, dataOut, out);
                } else if (method.equals("POST")) {
                    handlePostRequest(in, fileRequested, out);
                } else {
                    out.println("HTTP/1.1 501 Not Implemented");
                }
            }
        } catch (IOException e) {
            System.err.println("No se pudo procesar la solicitud.");
            System.exit(1);
        }
    }

    /**
     * Maneja una solicitud GET.
     * @param path     La ruta solicitada.
     * @param dataOut El flujo de salida para enviar los datos.
     * @param out El flujo de salida para enviar las cabeceras HTTP.
     */
    public static void handleGetRequest(String path, BufferedOutputStream dataOut, PrintWriter out) {

        String basePath = path.split("\\?")[0];

        if (services.containsKey(basePath)){
            System.out.println("Manejando ruta dinámica: " + basePath);

            HttpRequest req = new HttpRequest(path);
            HttpResponse res = new HttpResponse(out);

            String responseBody = services.get(basePath).apply(req, res);

            out.println("HTTP/1.1 200 OK");
            out.println("Content-Type: application/json");
            out.println();
            out.println(responseBody);
            System.out.println("GET " + path + " procesado exitosamente.");
            return;
        }

        // Manejar archivos estáticos
        File file = new File(staticFilesDirectory, path);
        if (file.exists() && !file.isDirectory()) {
            try {
                String contentType = getType(path);
                out.println("HTTP/1.1 200 OK");
                out.println("Content-Type: " + contentType);
                out.println();
                out.flush();

                FileInputStream fileInputStream = new FileInputStream(file);
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                    dataOut.write(buffer, 0, bytesRead);
                }
                dataOut.flush();
                fileInputStream.close();
                System.out.println("Archivo " + path + " enviado exitosamente.");
            } catch (IOException e) {
                out.println("HTTP/1.1 500 Internal Server Error");
                System.err.println("Error al enviar el archivo " + path + ": " + e.getMessage());
            }
        } else {
            out.println("HTTP/1.1 404 Not Found");
            System.err.println("Archivo no encontrado: " + path);
        }
    }

    /**
     * Maneja una solicitud POST.
     * Si la solicitud es para la API /api/updateName, actualiza el nombre en la memoria.
     * @param in El flujo de entrada para leer la solicitud.
     * @param path     La ruta solicitada..
     * @param out El flujo de salida para enviar las cabeceras HTTP.
     */
    private static void handlePostRequest(BufferedReader in, String path, PrintWriter out) {
        if(path.equals("/App/updateName")) {
            System.out.println("Manejando ruta dinámica: " + path);
            try {
                String line;
                int contentLength = 0;
                while (!(line = in.readLine()).isEmpty()) {
                    if (line.startsWith("Content-Length:")) {
                        contentLength = Integer.parseInt(line.split(":")[1].trim());
                    }
                }

                // Leer el cuerpo de la solicitud
                char[] body = new char[contentLength];
                in.read(body, 0, contentLength);
                String requestBody = new String(body);
                System.out.println("Nombre actualizado: " + requestBody);

                //Extrae el nuevo nombre y lo guarda
                String name = requestBody.toString().replace("{\"name\":\"", "").replace("\"}", "");
                dataStore.put("name", name);

                out.println("HTTP/1.1 200 OK");
                out.println("Content-Type: application/json");
                out.println();
                System.out.println("POST /App/updateName procesado exitosamente.");

            } catch (IOException e) {
                out.println("HTTP/1.1 500 Internal Server Error");
                System.err.println("Error procesando POST /api/updateName: " + e.getMessage());
            }
        }else{
            out.println("HTTP/1.1 404 Not Found");
            System.err.println("Endpoint no encontrado: " + path);
        }
    }

    /**
     * Devuelve el tipo MIME correspondiente a una extensión de archivo.
     * @param path
     * @return El tipo MIME del archivo.
     */
    private static String getType(String path) {
        String extension = getFileExtension(path);
        switch (extension) {
            case "html":
                return "text/html";
            case "css":
                return "text/css";
            case "js":
                return "application/javascript";
            case "png":
                return "image/png";
            case "jpg":
            case "jpeg":
                return "image/jpeg";
            default:
                return "application/octet-stream";
        }
    }

    /**
     * Extrae la extensión de un archivo.
     * @param path     La ruta solicitada.
     * @return La extensión del archivo.
     */
    private static String getFileExtension(String path) {
        int dotIndex = path.lastIndexOf('.');
        if (dotIndex == -1) {
            return "";
        }
        return path.substring(dotIndex + 1).toLowerCase();
    }

    /**
     * Registra una ruta GET y su manejador.
     * @param path    La ruta a registrar.
     * @param handler La función lambda que manejará la solicitud.
     */
    public static void get(String path, BiFunction<HttpRequest, HttpResponse, String> handler) {
        services.put(path, handler);
    }

    /**
     * Obtiene el mapa que contiene los datos clave-valor de tipo String.
     * @return El mapa `dataStore` con datos de tipo String.
     */
    public static Map<String, String> getDataStore() {
        return dataStore;
    }

    /**
     * Obtiene el mapa que contiene las funciones que aceptan un `HttpRequest` y un `HttpResponse`,
     * y retornan un `String`.
     * @return El mapa `services` con las funciones mapeadas.
     */
    public static Map<String, BiFunction<HttpRequest, HttpResponse, String>> getServices() {
        return services;
    }

}