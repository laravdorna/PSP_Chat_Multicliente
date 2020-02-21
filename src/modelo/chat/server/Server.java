package modelo.chat.server;


import java.io.*;
import java.net.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import modelo.Usuario;

/**
 * Este es el programa del servidor de chat. Presione Ctrl + C para terminar el
 * programa.
 *
 * @author Juan Martinez Piñeiro y Lara Vázquez Dorna
 */
public class Server {

    //atributos
    private int port;
    private Set<String> userNames = new HashSet<>();
    private Set<MultiServerThread> userThreads = new HashSet<>();

    /**
     * Constructor. se le pasa el parametro con el puerto de conexion
     *
     * @param port
     */
    public Server(int port) {
        this.port = port;
    }

    /**
     * Almacena el nombre de usuario del cliente recién conectado.
     */
    void addUserName(String userName) {
        userNames.add(userName);
    }

    //devuelve los nombres de los usuarios
    Set<String> getUserNames() {
        return this.userNames;
    }

    /**
     * Cuando un cliente está desconectado, elimina el nombre de usuario
     * asociado y MultiServerThread
     */
    void removeUser(String userName, MultiServerThread aUser) {
        boolean removed = userNames.remove(userName);
        if (removed) {
            userThreads.remove(aUser);
            System.out.println("The user " + userName + " quitted");
        }
    }

    /**
     * Devuelve verdadero si hay otros usuarios conectados (no se cuentan los
     * actuales usuario conectado)
     */
    boolean hasUsers() {
        return !this.userNames.isEmpty();
    }

    /**
     * Metodo que ejecuta el servidor. Avisa de cada nuevo usuario conectado y
     * lo añade al chat llamando a la clase multiserver.
     */
    public void execute() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {

            System.out.println("El servidor de chat está escuchando en el puerto " + port);
            //prueba de como funciona el Logger
            /* Logger.getLogger(Server.class.getName()).log(
             Level.INFO,
             "Chat Server is listening on port " + port
             );*/

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Nuevo usuario conectado");

                MultiServerThread newUser = new MultiServerThread(socket, this);
                userThreads.add(newUser);
                newUser.start();
            }
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Entrega un mensaje de un usuario a otros (transmisión)
     */
    void broadcast(String message, MultiServerThread excludeUser) {

        for (MultiServerThread aUser : userThreads) {
            if (aUser != excludeUser) {
                aUser.sendMessage(message);
            }
        }
    }

    /**
     * Metodo salveToFile. crea un fichero donde se guarda un registro de la
     * conversación guardada por dia. Especifica que dijo cada usuario y la hora
     * . tambien cuando se conecta
     *
     * @param message
     * @param excludeUser
     */
    void saveToFile(String message, MultiServerThread excludeUser) {
        FileWriter fileWriter = null;
        try {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            DateFormat timeFormat = new SimpleDateFormat("[EEE, HH:mm:ss] ");
            Date date = new Date();
            fileWriter = new FileWriter(dateFormat.format(date) + "_log.txt", true);
            String sep = System.getProperty("line.separator");
            fileWriter.write(timeFormat.format(date) + message + sep);
            fileWriter.close();
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                fileWriter.close();
            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Metodo main. Instancia un objeto de tipo servidor y llama al metodo
     * ejecutar
     *
     * @param args
     */
    public static void main(String[] args) {
        int port = 5000;
        
       
        Server server = new Server(port);
        server.execute();

    }

}
