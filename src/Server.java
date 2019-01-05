import javax.crypto.Cipher;
import javax.net.ssl.*;
import javax.security.cert.X509Certificate;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.FileInputStream;
import java.net.Socket;
import java.security.KeyStore;
import java.security.PublicKey;
import java.util.Objects;

/**
 * Created by Juan Cid on 05-01-2019.
 */
class Server {
    private static final int PORT = 1234;

    private static SSLServerSocketFactory getSSS() {
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

            return sc.getServerSocketFactory();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String args[]) {

        try {
            SSLServerSocket serverSocket = (SSLServerSocket) Objects.requireNonNull(getSSS())
                    .createServerSocket(PORT);
            serverSocket.setNeedClientAuth(true);
            System.out.println("¡Server OK!");

            while (true) {
                new ThreadResolver(serverSocket.accept()).start();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static class ThreadResolver extends Thread {
        final Socket socket;

        ThreadResolver(Socket socket) {
            this.socket = socket;
        }

        static byte[] decrypt(byte[] bytes, X509Certificate certificate) {
            PublicKey publicKey = certificate.getPublicKey();
            try {
                Cipher cipher = Cipher.getInstance("RSA");
                cipher.init(Cipher.DECRYPT_MODE, publicKey);
                return cipher.doFinal(bytes);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        static byte[] resBytes(DataInputStream inStream, DataOutputStream outStream) {
            byte[] message = null;
            try {
                int length = inStream.readInt();
                if (length > 0) {
                    message = new byte[length];
                    inStream.readFully(message, 0, message.length);
                    outStream.writeInt(message.length);
                    outStream.write(message);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return message;
        }

        static X509Certificate getCert(Socket socket) {
            SSLSocket sslSocket = (SSLSocket) socket;
            try {
                return sslSocket.getSession().getPeerCertificateChain()[0];
            } catch (SSLPeerUnverifiedException e) {
                e.printStackTrace();
            }
            return null;
        }

        static String getString(DataInputStream inStream, DataOutputStream outStream) {
            try {
                String msg = inStream.readUTF();
                if (!msg.isEmpty()) {
                    outStream.writeUTF(msg);
                    return msg;
                }
            } catch (EOFException e) {
                e.getCause();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "";
        }

        @Override
        public void run() {
            super.run();
            System.out.println("\tGot connection from client " + socket.getInetAddress());
            X509Certificate cert = getCert(socket);
            String username = Objects.requireNonNull(cert).getIssuerDN().getName();

            try {
                DataInputStream inStream = new DataInputStream(socket.getInputStream());
                DataOutputStream outStream = new DataOutputStream(socket.getOutputStream());
                String cHash;
                while (inStream.available() == 0) {
                    byte[] encryptedByteHash = resBytes(inStream, outStream);
                    if (encryptedByteHash != null) {
                        cHash = new String(Objects.requireNonNull(decrypt(encryptedByteHash, cert)));
                    } else break;

                    String msg = getString(inStream, outStream);
                    if (!msg.isEmpty()) {
                        String user = Util.getHash(msg).equals(cHash) ? username : "";
                        String data = Util.getTime() + " [" + user + "] says: \"" + msg + "\"";
                        System.out.println(data);
                    } else break;
                }

                socket.close();
                System.out.println("Socket closed");
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}
