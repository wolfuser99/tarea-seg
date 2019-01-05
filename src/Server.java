import javax.net.ssl.*;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.KeyStore;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Server {

    public static void main(String args[]) {
        SSLServerSocket serverSocket;

        try {

            KeyStore keyStore = KeyStore.getInstance("JKS");
            keyStore.load(new FileInputStream("app.jks"), "storepass".toCharArray());

            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(keyStore, "keypass".toCharArray());
            KeyManager[] keyManagers = kmf.getKeyManagers();

            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(keyStore);
            TrustManager[] trustManagers = tmf.getTrustManagers();


            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(keyManagers, trustManagers, null);

            SSLServerSocketFactory ssf = sc.getServerSocketFactory();
            serverSocket = (SSLServerSocket) ssf.createServerSocket(1234);
            serverSocket.setNeedClientAuth(true);
            System.out.println("¡Server OK!");

            while (true) {
                res(serverSocket.accept());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void res(Socket socket) {
        System.out.println("Client accepted:");

        SSLSocket sslSocket = (SSLSocket) socket;
        if (sslSocket.getSession().getLocalCertificates() != null) {
            SSLSession session = sslSocket.getSession();

            try {
                System.out.println(
                        "   " + session.getPeerCertificateChain()[0].getIssuerDN() +
                                "\n   Host Par es " + session.getPeerHost() +
                                "\n   Protocolo es " + session.getProtocol()
                );
            } catch (SSLPeerUnverifiedException e) {
                e.printStackTrace();
            }

        }
        try {
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String recibido = input.readLine();

            Calendar cal = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            System.out.println("   " + sdf.format(cal.getTime()) + " Mensaje recibido: " + recibido);

            PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
            output.println("echo: " + recibido);

            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
