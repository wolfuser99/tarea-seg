import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServer {
    private static int PUERTO = 1234;

    public static ServerSocket crearServerSocket(int puerto) throws IOException {
        return new ServerSocket(puerto);
    }

    public static void main(String args[]) {

        BufferedReader entrada;
        PrintWriter salida;
        ServerSocket server;

        try {

            // creamos server socket con SSL
            server = crearServerSocket(PUERTO);

            boolean stop = false;
            while (!stop) {
                System.out.println("Esperando una conexión...");
                Socket client = server.accept();

                System.out.println("Un cliente se ha conectado...");
                // Para los canales de entrada y salida de datos
                entrada = new BufferedReader(new InputStreamReader(client.getInputStream()));
                salida = new PrintWriter(client.getOutputStream(), true);

                String str = entrada.readLine();
                System.out.println("Confirmando recepcion de mensaje del cliente:" + str);
                salida.println(str);
                // Cerrando la conexión
                client.close();
            }//while
            server.close();
        } catch (IOException e) {
            System.out.println("Error de entrada/salida." + e.getMessage());
        }

    }//main
}

