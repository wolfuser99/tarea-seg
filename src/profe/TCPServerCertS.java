package profe;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;


public class TCPServerCertS {
    private static int PUERTO = 1234;

    public static SSLServerSocket crearServerSocket(int puerto) throws IOException {
        SSLServerSocketFactory factory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
        SSLServerSocket server = (SSLServerSocket) factory.createServerSocket(PUERTO);
        server.setEnabledCipherSuites(server.getSupportedCipherSuites());
        return server;
    }

    public static void main(String args[]) {

        BufferedReader entrada;
        PrintWriter salida;
        SSLServerSocket server;

        try {

            System.setProperty("javax.net.ssl.keyStore", "./llavesservidor.jks");
            System.setProperty("javax.net.ssl.keyStorePassword", "pwd123");
            //System.setProperty("javax.net.debug", "all");

            // creamos server socket con SSL
            server = crearServerSocket(PUERTO);

            boolean stop = false;
            while (!stop) {
                System.out.println("Esperando una conexión...");
                SSLSocket client = (SSLSocket) server.accept();
                //client.setEnabledCipherSuites(client.getSupportedCipherSuites());

                SSLSession session = client.getSession();
                Certificate[] cchain2 = session.getLocalCertificates();
                for (int i = 0; i < cchain2.length; i++) {
                    System.out.println(((X509Certificate) cchain2[i]).getSubjectDN());
                }
                System.out.println("Host Par es " + session.getPeerHost());
                System.out.println("Cifrador es " + session.getCipherSuite());
                System.out.println("Protocolo es " + session.getProtocol());
                System.out.println("ID es " + new BigInteger(session.getId()));
                System.out.println("Sesion fue creada en " + session.getCreationTime());
                System.out.println("Sesion fue acesdida en " + session.getLastAccessedTime());


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
        }

    }//main
}

