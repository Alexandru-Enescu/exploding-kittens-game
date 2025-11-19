package network.model;

import network.view.NetworkTUI;
import java.io.*;
import java.net.Socket;

/**
 * Class to represent a network human player in the Exploding Kittens game.
 * @author Alexandru-Cristian Enescu and Oliver Li
 */
public class NetworkPlayer implements Runnable {
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private final NetworkTUI NETWORK_TUI;

    /**
     * Create a human NetworkPlayer.
     * Initialize the NetworkTUI which is used to get player input.
     */
    public NetworkPlayer() {
        this.NETWORK_TUI = new NetworkTUI(this);
    }

    /**
     * Get the socket of the NetworkPlayer.
     * @return the socket used to communicate with the server
     */
    public Socket getSocket() {
        return socket;
    }

    /**
     * Get the BufferedReader of the NetworkPlayer.
     * @return the BufferedReader used to listen for messages from the server
     */
    public BufferedReader getBufferedReader() {
        return bufferedReader;
    }

    /**
     * Get the BufferedWriter of the NetworkPlayer.
     * @return the BufferedWriter used to send messages to the server
     */
    public BufferedWriter getBufferedWriter() {
        return bufferedWriter;
    }

    /**
     * This method will be used by the main thread to continuously get input from the player.
     */
    public void startToPlay() {
        NETWORK_TUI.getPlayerInput();
    }

    /**
     * Initialize the socket and connect to the server.
     * Initialize the buffered reader and the buffered writer.
     */
    public void connectToServer() {
        try {
            this.socket = new Socket("localhost", 5000);
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            System.out.println("Exception " + e);
        }
    }

    /**
     * This method is used to send messages to the server.
     * @param messageToSend the message which is sent
     */
    public void sendMessageToServer(String messageToSend) {
        try {
            bufferedWriter.write(messageToSend);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (IOException e) {
            System.out.println("Exception " + e);
        }
    }

    /**
     * This method is used to continuously listen for messages from the server.
     * After receiving a message, send it to the TUI.
     */
    @Override
    public void run() {
        String messageFromServer;
        try {
            messageFromServer = bufferedReader.readLine();
            while (messageFromServer != null) {
                NETWORK_TUI.printMessageFromServer(messageFromServer);
                messageFromServer = bufferedReader.readLine();
            }
        } catch (IOException e) {
            System.out.println("Exception " + e);
        }
    }

    public static void main(String[] args) {
        NetworkPlayer networkPlayer = new NetworkPlayer();
        networkPlayer.connectToServer();
        Thread thread = new Thread(networkPlayer);
        thread.start();
        networkPlayer.startToPlay();
    }
}
