package modelo.chat.client;

import consolereader.ConsoleReader;
import java.io.*;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Este hilo es responsable de leer la entrada del usuario y enviarla aservidor.
 * Se ejecuta en un bucle infinito hasta que el usuario escribe 'chao' para
 * salir.
 *
 * @author Lara Vazquez Dorna
 */
public class WriteThread extends Thread {

    //atributos
    private PrintWriter writer;
    private Socket socket;
    private Client client;

    /**
     * Contructor. Intenta abrir la comunicacion
     *
     * @param socket
     * @param client
     */
    public WriteThread(Socket socket, Client client) {
        this.socket = socket;
        this.client = client;

        try {
            OutputStream output = socket.getOutputStream();
            writer = new PrintWriter(output, true);
        } catch (IOException ex) {
            Logger.getLogger(WriteThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * metodo heredado de la interface Thread que te pide el nombre de usuario
     * y cuando lo tiene lo añade a todo lo que mande al servidor
     * sale del chat cuando escribes la palabra chao
     */
    @Override
    public void run() {
        ConsoleReader cr;
        String userName = new ConsoleReader("\nIntroduce tu nombre: ").read();
        client.setUserName(userName);
        writer.println(userName);

        String text;

        do {
            text = new ConsoleReader("[" + userName + "]: ").read();
            writer.println(text);

        } while (!text.equals("chao"));

        try {
            socket.close();
        } catch (IOException ex) {
            Logger.getLogger(WriteThread.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
