import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class TCPClient {

    private static String HOST = "localhost";
    private static int PUERTO = 1234;

    public static void main(String args[]) {

        Socket socket;
        PrintWriter salida;
        BufferedReader entrada, teclado;

        try {

            //Creamos nuestro socket
            socket = new Socket(HOST, PUERTO);

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
        }

    }
}
