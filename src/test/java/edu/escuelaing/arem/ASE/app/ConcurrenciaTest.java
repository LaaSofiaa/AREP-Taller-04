package edu.escuelaing.arem.ASE.app;

import org.junit.jupiter.api.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.*;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

public class ConcurrenciaTest {

    @BeforeAll
    public static void setUp() throws Exception {
        new Thread(() -> {
            try {
                App.main(new String[]{});
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
        Thread.sleep(2000);
    }

    @Test
    public void concurrencia() throws InterruptedException {
        int numThreads = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(numThreads);
        CountDownLatch latch = new CountDownLatch(numThreads); // Para esperar que todos terminen

        IntStream.range(0, numThreads).forEach(i ->
                executorService.submit(() -> {
                    try {
                        URL url = new URL("http://localhost:35000/App/greeting?name=Thread" + i);
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setRequestMethod("GET");

                        int responseCode = connection.getResponseCode();
                        assertEquals(200, responseCode, "El servidor no respondió correctamente");

                        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        String response = in.readLine();
                        in.close();

                        String expectedResponse = "Hola Thread" + i;
                        assertEquals(expectedResponse, response, "La respuesta no coincide");
                    } catch (Exception e) {
                        System.err.println("Error en la solicitud del hilo " + i + ": " + e.getMessage());
                    } finally {
                        latch.countDown();
                    }
                })
        );
        latch.await(); // Espera a que todos los hilos terminen
        executorService.shutdown();
    }
}
