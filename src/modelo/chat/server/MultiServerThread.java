package modelo.chat.server;

import java.io.*;
import java.net.*;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import modelo.Usuario;
import modelo.Usuarios;

/**
 * Este hilo es responsable de leer la entrada del servidor e imprimirla en
 * consola. Se ejecuta en un bucle infinito hasta que el cliente se desconecta
 * del servidor.
 *
 * @author Lara Vazquez Dorna
 */
public class MultiServerThread extends Thread {

    private Socket socket;
    private Server server;
    private PrintWriter writer;

    /**
     * Constructor. crea un servidor y un socket
     *
     * @param socket
     * @param server
     */
    public MultiServerThread(Socket socket, Server server) {
        this.socket = socket;
        this.server = server;
    }

    /**
     * metodo heredado de la interface Thread que imprime los usuarios
     * conectados y sus respuestas pasandoselas a los otros clientes
     */
    @Override
    public void run() {
        try {
            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));

            OutputStream output = socket.getOutputStream();
            writer = new PrintWriter(output, true);

            String stream = reader.readLine();
            String userName = "";
//comunicacion entre el server y client usa comandos reservados que interpreta con la base de datos
            if (stream.startsWith("/")) {
                String cmd = stream.substring(1, stream.indexOf(' '));
                String content = stream.substring(stream.indexOf(' ') + 1);

                if (cmd.equalsIgnoreCase("login")) {

                    String user = "";

                    user = content.substring(0, content.indexOf(' '));

                    user = content;
                    try {
                        // Usuarios us = new Usuarios();
                        // List<Usuario> usuarios = us.getUsuarios();
                        // System.out.println(usuarios);

                        Usuario u = new Usuario();
                        u.setNick(user);

                        //añade el usuario a la lista de conectados y se lo envias al cliente
                        userName = user;
                        server.addUserName(userName);
                        printUsers();

                        String serverMessage = "Nuevo usuario conectado: " + userName;
                        server.broadcast(serverMessage, this);
                        server.saveToFile(serverMessage, this);

                    } catch (Exception ex) {
                        Logger.getLogger(MultiServerThread.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else if (cmd.equalsIgnoreCase("logout")) {
                    String serverMessage = "El  usuario: " + userName + "ha dejado el chat";
                    server.broadcast(serverMessage, this);
                    server.saveToFile(serverMessage, this);

                }
            }
//empieza la conversacion entre clientes 
            String clientMessage;
            String serverMessage;
            boolean salir = false;
            do {

                clientMessage = reader.readLine();
                System.out.println(clientMessage);

                serverMessage = "[" + userName + "]: " + clientMessage;
                server.broadcast(serverMessage, this);
                server.saveToFile(serverMessage, this);

            } while (!salir);

            server.removeUser(userName, this);
            socket.close();

            printUsers();
            serverMessage = userName + " ha dejado el chat.";
            server.broadcast(serverMessage, this);

        } catch (IOException ex) {
            Logger.getLogger(MultiServerThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Envía una lista de usuarios en línea al usuario recién conectado.
     */
    void printUsers() {
        if (server.hasUsers()) {
            server.broadcast("/users " + String.join(",", server.getUserNames()), this);
            writer.println("/users " + String.join(",", server.getUserNames()));
        } else {
            writer.println("No hay otros usuarios conectados");
        }
    }

    /**
     * Envia el mensaje al cliente.
     */
    void sendMessage(String message
    ) {
        writer.println(message);
    }
}
