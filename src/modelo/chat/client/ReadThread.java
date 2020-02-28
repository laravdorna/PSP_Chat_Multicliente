package modelo.chat.client;

import java.io.*;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Este hilo es responsable de leer la entrada del servidor e imprimirla en
 * consola. Se ejecuta en un bucle infinito hasta que el cliente se desconecta
 * del servidor.
 *
 * @author Lara Vazquez Dorna
 */
public class ReadThread extends Thread {

    //atributos
    private BufferedReader reader;
    private Socket socket;
    private Client client;

    /**
     * Constuctor. Envia los parametros y crea la conexion de escucha
     *
     * @param socket
     * @param client
     */
    public ReadThread(Socket socket, Client client) {
        this.socket = socket;
        this.client = client;

        try {
            InputStream input = socket.getInputStream();
            reader = new BufferedReader(new InputStreamReader(input));
        } catch (IOException ex) {
            Logger.getLogger(ReadThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * metodo heredado de la interface Thread que imprime la respuesta del
     * servidor segun el cliente que la mande
     */
    @Override
    public void run() {
        while (true) {
            try {
                String response = reader.readLine();
                System.out.println("\n" + response);

                //imprime el nombre del usuario despues de mostrar el mensaje del servidor
                if (client.getUserName() != null) {
                    System.out.print("[" + client.getUserName() + "]: ");
                }
            } catch (IOException ex) {
                Logger.getLogger(ReadThread.class.getName()).log(Level.SEVERE, null, ex);
                break;
            }
        }
    }
}
