package profe;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class TCPServerS {
    private static int PUERTO = 1234;

    public static SSLServerSocket crearServerSocket(int puerto) throws IOException {
        SSLServerSocketFactory factory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
        SSLServerSocket server = (SSLServerSocket) factory.createServerSocket(PUERTO);
        //printAvailableCipherSuites(server);
        return server;
    }


    public static void main(String args[]) {

        BufferedReader entrada;
        PrintWriter salida;
        SSLServerSocket server;

        try {

            System.setProperty("javax.net.ssl.keyStore", "./llavesservidor.jks");
            System.setProperty("javax.net.ssl.keyStorePassword", "pwd123");

            // creamos server socket con SSL
            server = crearServerSocket(PUERTO);

            boolean stop = false;
            while (!stop) {
                System.out.println("Esperando una conexión...");
                SSLSocket client = (SSLSocket) server.accept();
                //client.setEnabledCipherSuites(client.getSupportedCipherSuites());

                System.out.println("Un cliente se ha conectado...");
                // Para los canales de entrada y salida de datos
                entrada = new BufferedReader(new InputStreamReader(client.getInputStream()));
                salida = new PrintWriter(client.getOutputStream(), true);

                String str = entrada.readLine();
                System.out.println("Confirmando recepcion de mensaje del cliente:" + str);
                salida.println(str);
                client.close();
            }//while
            // Cerrando la conexión
            server.close();
        } catch (IOException e) {
            System.out.println("Error de entrada/salida." + e.getMessage());
            e.printStackTrace();
        }
    }//main

    private static void printAvailableCipherSuites(SSLServerSocket ss) {
        String[] suites = ss.getSupportedCipherSuites();
        if (suites != null)
            for (int i = 0; i < suites.length; ++i)
                System.out.println(suites[i]);

    }
}

