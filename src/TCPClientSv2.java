import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import java.io.*;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;


public class TCPClientSv2 {

    private static String HOST = "localhost";
    private static int PUERTO = 1234;
    private static String trustStore = "truststore_cliente.jks";
    private static char trustStorePass[] = "pwd123".toCharArray();

    public static void main(String args[]) {

        SSLSocket socket;
        PrintWriter salida;
        BufferedReader entrada, teclado;

        try {
            KeyStore ts = KeyStore.getInstance("JKS");
            ts.load(new FileInputStream(trustStore), trustStorePass);
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(ts);

            //Creamos nuestro socket con SSL
            SSLContext context = SSLContext.getInstance("TLSv1.2");
            context.init(null, tmf.getTrustManagers(), null);
            SSLSocketFactory factory = context.getSocketFactory();
            socket = (SSLSocket) factory.createSocket(HOST, PUERTO);
            socket.setEnabledCipherSuites(socket.getSupportedCipherSuites());

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
        } catch (NoSuchAlgorithmException e) {
            System.out.println("No existe el algoritmo.");
        } catch (KeyManagementException e) {
            System.out.println("Error en manejo de la Llave");
        } catch (KeyStoreException e) {
            System.out.println("Error en KeyStore.");
        } catch (CertificateException e) {
            System.out.println("Error en Certificado.");
        }

    }
}
