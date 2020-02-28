package modelo.chat.client;

import java.net.*;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Cliente del programa Tipo 'chao' para salir del chat.
 *
 * @author Lara Vazquez Dorna
 */
public class Client {

    //atributos
    private String hostname;//IP
    private int port;//Puerto de escucha
    private String userName;// nombre de usuario 
    private String password;
    /**
     * Constructor al que se le pasan obligatoriamente los parametros de
     * conexion
     *
     * @param hostname
     * @param port
     */
    public Client(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
    }

    /**
     * Metodo que crea la conexión con el servidor Y Llama a los metodos de
     * lectura y escritura de hilo
     */
    public void execute() {
        try {
            Socket socket = new Socket(hostname, port);

            System.out.println("Conectado al chat ");

            new ReadThread(socket, this).start();
            new WriteThread(socket, this).start();
            //cambiar las excepciones a logger
        } catch (UnknownHostException ex) { 
            Logger.getLogger(Client.class.getName()).log(
                    Level.INFO,"El servidor no funciona. " );
        } catch (IOException ex) {
              Logger.getLogger(Client.class.getName()).log(
                    Level.INFO,"Error de entrada y salida. " );
        }

    }
    
    //metodos getter y setter
    void setUserName(String userName) {
        this.userName = userName;
    }

    String getUserName() {
        return this.userName;
    }

    // metodo main que  comunica el cliente con el servidor
    public static void main(String[] args) {
        //variables de puerto y host
        String hostname = "localhost";
        int port = 5000;
        //intancia un objeto cliente y llama al metodo ejecutar chat
        Client client = new Client(hostname, port);
        client.execute();
    }
}
