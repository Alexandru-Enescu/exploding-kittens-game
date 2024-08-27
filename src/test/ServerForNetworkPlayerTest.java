package test;

import exceptions.E12;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Class used for the NetworkPlayerTest class. This class must be run before the running the NetworkPlayerTest class.
 * @author Alexandru-Cristian Enescu and Oliver Li
 */
public class ServerForNetworkPlayerTest {
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private final ServerSocket serverSocket;

    /**
     * Create the ServerSocket.
     */
    public ServerForNetworkPlayerTest() throws E12 {
        try {
            this.serverSocket = new ServerSocket(5000);
        } catch (IOException e) {
            throw new E12();
        }
    }

    /**
     * This method tests the connection, if the NetworkPlayer connects successfully to the server, its Socket, BufferedReader
     * and BufferedWriter are initialized.
     */
    public void listenForConnection() throws E12 {
        try {
            this.socket = serverSocket.accept();
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            throw new E12();
        }
    }

    /**
     * This method is used to test the communication. It first sends "Test message 1" to check if the NetworkPlayer receives it.
     * It waits for a message containing "Test message 2." from the NetworkPlayer, and when receives it, send a confirmation.
     */
    public void listenForMessageAndSendMessage() throws E12 {
        try {
            bufferedWriter.write("Test message 1.");
            bufferedWriter.newLine();
            bufferedWriter.flush();

            String messageFromNetworkPlayer = this.bufferedReader.readLine();
            if(messageFromNetworkPlayer.equals("Test message 2.")) {
                this.bufferedWriter.write("Message 2 received.");
                this.bufferedWriter.newLine();
                this.bufferedWriter.flush();
            }
        } catch (IOException e) {
            throw new E12();
        }
    }

    public static void main(String[] args) {
        try {
            ServerForNetworkPlayerTest testServer = new ServerForNetworkPlayerTest();
            testServer.listenForConnection();
            testServer.listenForMessageAndSendMessage();
        } catch (E12 e) {
            System.out.println(e.getMessage());
        }
    }
}
