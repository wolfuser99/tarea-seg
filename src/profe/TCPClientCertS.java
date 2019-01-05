package profe;

import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.UnknownHostException;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;

public class TCPClientCertS {

    private static String HOST = "localhost";
    private static int PUERTO = 1234;

    public static void main(String args[]) {

        SSLSocket socket;
        PrintWriter salida;
        BufferedReader entrada, teclado;

        try {

            System.setProperty("javax.net.ssl.trustStore", "./truststore_cliente.jks");
            System.setProperty("javax.net.ssl.trustStorePassword", "pwd123");
            //System.setProperty("javax.net.debug", "all");


            //Creamos nuestro socket con SSL
            SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            socket = (SSLSocket) factory.createSocket(HOST, PUERTO);
            socket.setEnabledCipherSuites(socket.getSupportedCipherSuites());

            SSLSession session = socket.getSession();
            Certificate[] cchain = session.getPeerCertificates();
            System.out.println("Los Certificados usados por el par");
            for (int i = 0; i < cchain.length; i++) {
                System.out.println(((X509Certificate) cchain[i]).getSubjectDN());
            }
            System.out.println("Host par " + session.getPeerHost());
            System.out.println("Cifrador " + session.getCipherSuite());
            System.out.println("Protocolo " + session.getProtocol());
            System.out.println("ID es " + new BigInteger(session.getId()));
            System.out.println("Sesion fue creada en " + session.getCreationTime());
            System.out.println("Sesion fue accedida en " + session.getLastAccessedTime());

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
}
