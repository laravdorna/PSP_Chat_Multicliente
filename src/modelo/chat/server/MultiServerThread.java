package modelo.chat.server;


import java.io.*;
import java.net.*;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import modelo.Usuario;

/**
 * Este hilo es responsable de leer la entrada del servidor e imprimirla en
 * consola. Se ejecuta en un bucle infinito hasta que el cliente se desconecta
 * del servidor.
 *
 * @author Juan Martinez Piñeiro y Lara Vázquez Dorna
 */
public class MultiServerThread extends Thread {

    private Socket socket;
    private Server server;
    private PrintWriter writer;
    private Usuario usuarioRegistrado;

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
                    String passwd = "";

                    user = content.substring(0, content.indexOf(' '));
                    passwd = content.substring(content.indexOf(' ') + 1);
                    //user = content;
                    try {
                        //me conecto a base de datos y compruebo en el DAO si existe el usuario, si no existe lo crea
                        ServerDAO serverdao = new ServerDAO();
                        List<Usuario> usuarios = serverdao.getUsuarios();
                       // System.out.println(usuarios);

                        Usuario u = new Usuario();
                        u.setNick(user);
                        
                       // usuarioRegistrado = serverdao.getUsuario(u);//comprueba si el usuario existe y devuelve el usuario de la db
                       // System.out.println("Usuario registrado: " + usuarioRegistrado);

                        //si el usuario no existe lo da de alta el la db
                        if (usuarioRegistrado == null) {
                           // boolean creado = serverdao.altaUsuario(u);
                            //una vez creado lo lee de la db
                         //   if (creado) {
                               // usuarioRegistrado = serverdao.getUsuario(u);
                            }
                        }

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

                }
            }
//empieza la conversacion entre clientes 
            String clientMessage;
            String serverMessage;
            boolean salir = false;
            do {

                clientMessage = reader.readLine();
                System.out.println(clientMessage);
                if (clientMessage.startsWith("/")) {
                    String cmd = clientMessage.substring(1, clientMessage.indexOf(' '));
                    System.out.println(cmd);
                    switch (cmd) {
                        case "logout":
                            salir = true;
                            break;
                        case "update":
                            String content = clientMessage.substring(clientMessage.indexOf(' ') + 1);
                            String user = "";
                            String passwd = "";

                            user = stream.substring(0, stream.indexOf(' '));
                            passwd = stream.substring(stream.indexOf(' ') + 1);
                            ServerDAO serverdao = new ServerDAO();
                            Usuario u = new Usuario();
                            u.setNick(user);
                            u.setPassword(passwd);
                            boolean modifica = serverdao.modificarUsuario(u);
                            //me conecto a base de datos, compruebo en el DAO, y actualizo
                            if (modifica) {
                                usuarioRegistrado = u;
                            }
                    }
                } else {
                    serverMessage = "[" + userName + "]: " + clientMessage;
                    server.broadcast(serverMessage, this);
                    server.saveToFile(serverMessage, this);
                }
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
    void sendMessage(String message) {
        writer.println(message);
    }
}
