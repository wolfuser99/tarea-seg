import javax.crypto.Cipher;
import javax.net.ssl.*;
import java.io.*;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.util.Objects;

/**
 * Created by Juan Cid on 05-01-2019.
 */
class Client {
    private static final int PORT = 1234;
    private static final String HOST = "localhost";
    private static PrivateKey privatekey;

    private static SSLSocketFactory getSSF(String path, String pass) {
        try {
            KeyStore keyStore = KeyStore.getInstance("JKS");
            keyStore.load(new FileInputStream(path), pass.toCharArray());

            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(keyStore, pass.toCharArray());

            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(keyStore);

            privatekey = (PrivateKey) keyStore
                    .getKey("keyjuan", "mypass".toCharArray());

            SSLContext sc = SSLContext.getInstance("TLS");
            TrustManager[] trustManagers = tmf.getTrustManagers();
            KeyManager[] keyManagers = kmf.getKeyManagers();
            sc.init(keyManagers, trustManagers, null);

            return sc.getSocketFactory();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static byte[] encrypt(String text) {
        Cipher rsaCipher;
        try {
            rsaCipher = Cipher.getInstance("RSA");
            rsaCipher.init(Cipher.ENCRYPT_MODE, privatekey);
            return rsaCipher.doFinal(text.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String args[]) {
        try {
            @SuppressWarnings("SpellCheckingInspection")
            SSLSocket client = (SSLSocket) Objects.requireNonNull(
                    getSSF("juan.jks", "mypass")
            ).createSocket(HOST, PORT);
            client.startHandshake();

            DataInputStream inStream = new DataInputStream(client.getInputStream());
            DataOutputStream outStream = new DataOutputStream(client.getOutputStream());
            BufferedReader fromKeyboard = new BufferedReader(new InputStreamReader(System.in));

            String input = " ";
            String hash;
            while (!input.isEmpty()) {
                System.out.println(
                        "\tIngresa un mensaje (anteponer \"- \" para quitar la firma)"
                                + " o presiona enter para finalizar:"
                );
                input = fromKeyboard.readLine();
                if (input.contains("- ")) {
                    hash = "";
                } else
                    hash = Util.getHash(input);
                byte[] encBytesHash = encrypt(hash);

                outStream.writeInt(Objects.requireNonNull(encBytesHash).length);
                outStream.write(encBytesHash);

                int echoLength = inStream.readInt();
                if (echoLength > 0) {
                    byte[] bytesEchoHash = new byte[echoLength];
                    inStream.readFully(bytesEchoHash, 0, bytesEchoHash.length);
                }

                if (!input.isEmpty()) {
                    outStream.writeUTF(input);
                } else break;
                String echo = inStream.readUTF();

                System.out.println(Util.getTime() + " { hash: " + hash + ", msg: \"" + echo + "\"}\n");
            }

            System.out.println("Client end");
            client.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
