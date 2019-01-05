import javax.net.ssl.*;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.security.KeyStore;

public class Client {

    public static void main(String args[]) {
        try {

            KeyStore keyStore = KeyStore.getInstance("JKS");
            keyStore.load(new FileInputStream("juan.jks"), "antonio13".toCharArray());

            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(keyStore, "antonio13".toCharArray());

            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(keyStore);

            SSLContext sc = SSLContext.getInstance("TLS");
            TrustManager[] trustManagers = tmf.getTrustManagers();
            KeyManager[] keyManagers = kmf.getKeyManagers();
            sc.init(keyManagers, trustManagers, null);

            SSLSocketFactory ssf = sc.getSocketFactory();
            SSLSocket client = (SSLSocket) ssf.createSocket("localhost", 1234);
            client.startHandshake();


            PrintWriter output = new PrintWriter(client.getOutputStream(), true);
            BufferedReader input = new BufferedReader(new InputStreamReader(client.getInputStream()));

            output.println("Federico");
            System.out.println("Federico sent");

            String received = input.readLine();
            System.out.println("Received : " + received);

            client.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
