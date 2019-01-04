import javax.net.ServerSocketFactory;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLSocket;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.security.KeyStore;

public class TCPServerSv2 {
    private static int PUERTO = 1234;
    private static String keyStore = "llavesservidor.jks";
    private static char keyStorePass[] = "pwd123".toCharArray();
    private static char keyPassword[] = "pwd123".toCharArray();

    public static SSLServerSocket crearServerSocket(int puerto) throws Exception {
        KeyStore ks = KeyStore.getInstance("JKS");
        ks.load(new FileInputStream(keyStore), keyStorePass);
        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm()); //SunX509
        kmf.init(ks, keyPassword); //usar solo una password para todas las llaves

        SSLContext sslcontext = SSLContext.getInstance("TLSv1.2");
        sslcontext.init(kmf.getKeyManagers(), null, null);
        ServerSocketFactory factory = sslcontext.getServerSocketFactory();
        SSLServerSocket server = (SSLServerSocket) factory.createServerSocket(PUERTO);
        server.setEnabledCipherSuites(server.getSupportedCipherSuites());
        return server;
    }

    public static void main(String args[]) {

        BufferedReader entrada;
        PrintWriter salida;
        SSLServerSocket server;

        try {

            //System.setProperty("javax.net.ssl.keyStore",keyStore);
            //System.setProperty("javax.net.ssl.keyStorePassword","pwd123");

            // creamos server socket con SSL
            server = crearServerSocket(PUERTO);
            server.setNeedClientAuth(false);

            boolean stop = false;
            while (!stop) {
                System.out.println("Esperando una conexión...");
                SSLSocket client = (SSLSocket) server.accept();

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

        } catch (Exception e) {
            System.out.println("Excepcion." + e.getMessage());
            e.printStackTrace();
        }

    }//main
}

