
# Taller de de modularización con virtualización e Introducción a Docker

En este taller se desarrolló y desplegó una aplicación web, utilizando un framework propio, con mejoras en concurrencia y manejo de apagado seguro. La aplicación fue **dockerizada** y publicada en `DockerHub`,
para luego ser desplegada en **AWS EC2** utilizando `Docker`. Se configuraron reglas de seguridad en la nube y se realizaron pruebas para verificar el correcto funcionamiento del servicio, asegurando un despliegue eficiente,
escalable y accesible desde una URL pública.



## Tabla de Contenido

1. [Instalación](#instalación)  
2. [Arquitectura del Proyecto](#arquitectura-del-proyecto)  
   - [Estructura del Directorio](#estructura-del-directorio)   
   - [Componentes del Proyecto](#componentes-del-proyecto)  
3. [Concurrencia y Apagado Seguro](#concurrencia-y-apagado-seguro)  
4. [Docker Despliegue Local](#docker-despliegue-local)  
5. [Docker y AWS](#docker-y-aws)  
6. [Pruebas Automatizadas](#pruebas-automatizadas)  
7. [Autor](#autor) 

  
## Instalación

**1.**  Clonar el repositorio

```bash
  git clone https://github.com/LaaSofiaa/AREP-Taller-04.git

  cd AREP-Taller-04
```
**2.**  Construir el proyecto mediante maven, donde debes tener previamente instalado este https://maven.apache.org . Luego pruebe el siguiente comando para compilar, empaquetar y ejecutar. 
```bash
  mvn clean install
  mvn package
```  
**3.**  Ejecuta el proyecto con el siguiente comando:
```bash
  java -cp target/Taller4-1.0-SNAPSHOT.jar edu.escuelaing.arem.ASE.app.App
```
o este: 
```bash
  java -cp "target/classes/" edu.escuelaing.arem.ASE.app.App
```

**4.**  Una vez este corriendo la aplicación prueba los siguiente:

* **Página Principal:**
```bash
  http://localhost:35000/
```


## Arquitectura del Proyecto 
### **Estructura del directorio**

El directorio del proyecto esta organizado de la siguiente manera:

```plaintext
src/
├── main/
│   ├── java/
│   │   ├── edu.escuelaing.arem.ASE.app/
│   │   │   ├── framework/
│   │   │   │   ├── annotations/
│   │   │   │   │   ├── GetMapping
│   │   │   │   │   ├── RequestMapping
│   │   │   │   │   ├── RequestParam
│   │   │   │   │   ├── RestController
│   │   │   │   │   ├── SpringSofiaScan
│   │   │   │   ├── config/
│   │   │   │   │   ├── SpringSofiaApp
│   │   │   │   ├── controllers/
│   │   │   │   │   ├── GreetingController
│   │   │   │   │   ├── MathController
│   │   │   │   ├── http/
│   │   │   │   │   ├── HttpRequest
│   │   │   │   │   ├── HttpResponse
│   │   │   │   │   ├── HttpServer
│   │   │   ├── App
│   ├── resources/
│   │   ├── app.html
│   │   ├── img.jpg
│   │   ├── index.html
│   │   ├── script.js
│   │   ├── styles.css
├── test/
│   ├── java/
│   │   ├── edu.escuelaing.arem.ASE.app/
│   │   │   ├── AppTest
│   │   │   ├── FrameworkTest
│   │   │   ├── ConcurrenciaTest
```

### **Componentes del Proyecto**

 `App.java`
- Contiene el método principal main que inicia el servidor.
- Permite servir archivos estáticos desde `src/main/java/resources`.

 `HttpServer.java`
- Implementa un servidor HTTP básico en el puerto 35000.
- Maneja solicitudes GET para servir archivos y ejecutar rutas dinámicas.
- Maneja solicitudes POST para actualizar valores en memoria.
- Encuentra controladores anotados con @RestController.
- Usa reflexión para llamar métodos que manejan rutas definidas.
- Implementa concurrencia en la gestión de solicitudes.

 `HttpRequest.java`
- Representa una solicitud HTTP.
- Analiza la URL y extrae los parámetros de consulta.

 `HttpResponse.java`
- Encapsula la respuesta HTTP y permite enviar datos al cliente.

`Anotaciones` 
- Incluye anotaciones personalizadas como @SpringSofiaScan, @RestController, @GetMapping, @RequestMapping y @RequestParam para definir rutas y parámetros de servicios web.

`Servicios`
- Los servicios de ejemplo como GreetingController y MathController que se cargan dinámicamente y exponen puntos finales REST.

### Concurrencia

Para mejorar el rendimiento, se implementó concurrencia en la gestión de solicitudes, asegurando que múltiples peticiones puedan procesarse simultáneamente sin bloqueos.

 **Técnicas utilizadas:**
 
  - ThreadPool: Se usó un ExecutorService para gestionar hilos de manera eficiente.
  - Manejo de hilos independientes: Cada petición HTTP es manejada por un hilo separado.
  - Sincronización eficiente: Se evitaron condiciones de carrera y bloqueos innecesarios.
    
    ![image](https://github.com/user-attachments/assets/c4ff2798-28e4-48ac-a8ed-dd80a15aca1b)
    
### Apagado Elegante

Se implementó un mecanismo de apagado controlado para asegurar que los recursos se liberen correctamente al detener el servidor.

**Mecanismo usado:**

  - Captura de señales de interrupción (CTRL+C, SIGTERM).
  - Cierre adecuado de sockets para evitar puertos ocupados después de la detención.
  - Liberación de recursos (hilos, conexiones abiertas, buffers de memoria).

    ![image](https://github.com/user-attachments/assets/b4493796-48b7-4644-88ba-adaa3e85ce62)



## Docker despliegue local

  1. En la raíz del proyecto, crea un archivo llamado `Dockerfile`

     ![image](https://github.com/user-attachments/assets/188eebab-7389-41d5-8aba-83c983e854b9)

  2. Ejecuta el siguiente comando en la terminal dentro de la carpeta del proyecto con `docker images` verificamos que se creo

     ![image](https://github.com/user-attachments/assets/2f3d19d3-9338-406a-97bb-8bb48c862205)
     

  4. Ahora, ejecuta tres instancias de la aplicación usando la imagen creada y con `docker ps` verificamos que se crearan

     ```bash
        docker run -d -p 34000:35000 --name app1 mi-aplicacion
        docker run -d -p 34001:35000 --name app2 mi-aplicacion
        docker run -d -p 34002:35000 --name app3 mi-aplicacion
     ```
  
   ![image](https://github.com/user-attachments/assets/87f15cb9-0628-4704-8c83-12b516dea2a7)

 
  5. Probamos que este funcionando

     ![image](https://github.com/user-attachments/assets/e311ab72-f435-44fe-929a-2556200d0206)
     ![image](https://github.com/user-attachments/assets/0ac5f3cc-04c2-4153-a828-05711d235ee8)
     
     
## Docker y AWS

[Ver video de demostración](https://youtu.be/QlU1eKl3LXI)


https://github.com/user-attachments/assets/d42b6113-a425-415c-b2f8-fa7a3b0f9600




## Pruebas Automatizadas

### 1. AppTest.java  
Esta clase, ubicada en edu.escuelaing.arem.ASE.app, se encarga de probar aspectos clave del funcionamiento del servidor,
asegurando que maneje correctamente las solicitudes HTTP y la carga de archivos estáticos.A través de pruebas unitarias, valida la correcta extracción de parámetros en las solicitudes (HttpRequest), 
la correcta generación de respuestas (HttpResponse) y la adecuada entrega de archivos estáticos como HTML, CSS, JavaScript e imágenes.
De esta manera, garantiza que el servidor responda de forma precisa y eficiente a las peticiones de los clientes.

---

### 2. FrameworkTest.java  
Esta clase, ubicada en edu.escuelaing.arem.ASE.app, valida el correcto funcionamiento del framework, asegurando que los controladores y rutas respondan adecuadamente a las solicitudes del usuario.
A través de pruebas en GreetingController, verifica que los endpoints devuelvan respuestas personalizadas basadas en los parámetros recibidos.
En MathController, comprueba la precisión de los cálculos matemáticos, evaluando constantes como Euler y Pi, así como operaciones básicas de suma y resta.
Finalmente, la prueba de error 404 garantiza que las solicitudes a rutas inexistentes sean manejadas correctamente, evitando respuestas inesperadas y asegurando la robustez del servidor.


---
### 3. ConcurrenciaTest.java 

Para ejecutar correctamente las pruebas en el servidor, es fundamental asegurarse de que **NO haya una instancia previa en ejecución**, ya que el servidor se inicia automáticamente en la prueba dentro del método @BeforeAll utilizando App.main(new String[]{}).
Si el servidor ya está corriendo, el intento de iniciar una nueva instancia en el mismo puerto (35000) generará un error del tipo "Address already in use: bind", debido a que dos procesos no pueden ocupar el mismo puerto simultáneamente.

Además, la prueba de concurrencia está diseñada para evaluar la capacidad del servidor de manejar múltiples solicitudes simultáneamente sin bloqueos ni errores. 
Para ello, se utiliza un ExecutorService con un FixedThreadPool de 10 hilos, enviando solicitudes HTTP concurrentes y verificando que cada una reciba la respuesta esperada.
También se emplea CountDownLatch para sincronizar la finalización de todas las solicitudes antes de cerrar los hilos de ejecución.
Esto permite validar que el servidor responde correctamente bajo carga y garantiza su correcto funcionamiento en escenarios reales con múltiples usuarios accediendo al mismo tiempo.

---
Para correr las pruebas usamos el siguiente comando

```bash
  mvn test

```
![image](https://github.com/user-attachments/assets/160754d7-f791-448e-98ea-12ae26680e4e)
![image](https://github.com/user-attachments/assets/2c1e5c95-b528-4961-8c88-0ad44738cfd7)
![image](https://github.com/user-attachments/assets/720e9891-12f4-4f82-a574-28113aab9bd4)
![image](https://github.com/user-attachments/assets/0633ae2c-0256-40f5-8503-b29c1ae3935f)
![image](https://github.com/user-attachments/assets/883dcb28-9514-47c7-9541-9bf40376ab57)



## Autor

**Laura Gil** - Desarrolladora y autora del proyecto. 

