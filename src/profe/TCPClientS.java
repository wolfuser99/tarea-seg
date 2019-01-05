package profe;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.UnknownHostException;
//import oracle.security.crypto.cert.X500Name;

public class TCPClientS {

    private static String HOST = "localhost";
    private static int PUERTO = 1234;

    public static void main(String args[]) {

        SSLSocket socket;
        PrintWriter salida;
        BufferedReader entrada, teclado;

        try {

            System.setProperty("javax.net.ssl.trustStore", "./truststore_cliente.jks");
            System.setProperty("javax.net.ssl.trustStorePassword", "pwd123");


            //Creamos nuestro socket con SSL
            SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            socket = (SSLSocket) factory.createSocket(HOST, PUERTO);
            //printAvailableCipherSuites(socket);

            teclado = new BufferedReader(new InputStreamReader(System.in));
            entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            salida = new PrintWriter(socket.getOutputStream(), true);

            //Leyendo palabra desde teclado
            String palabra = teclado.readLine();

            //Enviamos palabra
            salida.println(palabra);
            //leemos el retorno de la palabra desde el socket
            String eco = entrada.readLine();
            System.out.println("Respuesta desde el servidor: " + eco);

            //Cerramos la conexión
            socket.close();

        } catch (UnknownHostException e) {
            System.out.println("El host no existe o no está activo.");
        } catch (IOException e) {
            System.out.println("Error de entrada/salida.");
            e.printStackTrace();
        }

    }

    private static void printAvailableCipherSuites(SSLSocket s) {
        String[] suites = s.getSupportedCipherSuites();
        if (suites != null)
            for (int i = 0; i < suites.length; ++i)
                System.out.println(suites[i]);

    }
}
