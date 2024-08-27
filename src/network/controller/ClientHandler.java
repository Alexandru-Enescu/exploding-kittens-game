package network.controller;

import exceptions.*;
import protocol.ProtocolCommands;
import java.io.*;
import java.net.Socket;
import static local.view.ANSI.*;

/**
 * Class to represent the ClientHandler which receives and sends messages to the players.
 * @author Alexandru-Cristian Enescu and Oliver Li
 */
public class ClientHandler implements Runnable {
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String name;
    private final Server explodingKittensServer;

    /**
     * Create a ClientHandler and initialize its socket and server.
     * @param socket the socket used to communicate with the client
     * @param explodingKittensServer the server which has created this ClientHandler
     */
    public ClientHandler(Socket socket, Server explodingKittensServer) {
        this.socket = socket;
        this.explodingKittensServer = explodingKittensServer;
        try {
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            System.out.println("Exception " + e);
        }
    }

    public synchronized void setBufferedReader(BufferedReader bufferedReader) {
        this.bufferedReader = bufferedReader;
    }

    public synchronized void setBufferedWriter(BufferedWriter bufferedWriter) {
        this.bufferedWriter = bufferedWriter;
    }

    public synchronized void setSocket(Socket socket) {
        this.socket = socket;
    }

    public synchronized void setName(String name) {
        this.name = name;
    }

    public synchronized String getName() {
        return name;
    }

    public synchronized Server getExplodingKittensServer() {
        return explodingKittensServer;
    }

    @Override
    public void run() {
        String messageFromClient;
        try {
            messageFromClient = bufferedReader.readLine();
            while (messageFromClient != null) {
                if(this.getName() == null) {
                    System.out.println(YELLOW_BOLD + "Message received: " + RESET
                            + WHITE_BOLD + messageFromClient + RESET);
                } else {
                    System.out.println(YELLOW_BOLD + this.getName() + " -> server: " + RESET
                            + WHITE_BOLD + messageFromClient + RESET);
                }
                sendResponseToClient(messageFromClient);
                messageFromClient = bufferedReader.readLine();
            }
        } catch (IOException e) {
            System.out.println("Exception " + e);
        }
    }


    /**
     * This method is used when the NetworkPlayer or NetworkComputerPlayer sends a message to the server.
     * In this case, a method of the ExplodingKittensServer class is called, which will make a change in the NetworkGame.
     * When the message from the player is read, a response is sent to the player.
     * @param previousMessageFromClient the message received from the player
     * @requires previousMessageFromClient != null
     */
    public synchronized void sendResponseToClient(String previousMessageFromClient) throws IOException {
        String[] messageToArray = previousMessageFromClient.split(ProtocolCommands.ARGUMENT_SEPARATOR);

        String command = messageToArray[0];
        String argument1 = null;
        String argument2 = null;

        if(messageToArray.length > 1) {
            argument1 = messageToArray[1];
        }

        if(messageToArray.length > 2) {
            argument2 = messageToArray[2];
        }

        switch (command) {
            case ProtocolCommands.CONNECT_TO_SERVER:
                try {
                    if(explodingKittensServer.getConnectedPlayersNames().contains(argument1)) {
                        throw new E02();
                    }
                    this.name = argument1;
                    explodingKittensServer.sendHello(this, argument2);
                } catch (E09 | E02 e) {
                    sendMessageToClient(ProtocolCommands.ERROR + ProtocolCommands.ARGUMENT_SEPARATOR + e);
                }
                break;
            case ProtocolCommands.ADD_COMPUTER_PLAYER:
                explodingKittensServer.addComputerPlayer();
                break;
            case ProtocolCommands.REMOVE_COMPUTER_PLAYER:
                try {
                    explodingKittensServer.removeComputerPlayer();
                } catch (E06 e) {
                    sendMessageToClient(ProtocolCommands.ERROR + ProtocolCommands.ARGUMENT_SEPARATOR + e);
                }
                break;
            case ProtocolCommands.REQUEST_GAME:
                try {
                    explodingKittensServer.startNewGame(argument1, this);
                } catch (E05 | E08 | E13 | E11 | NumberFormatException e) {
                    sendMessageToClient(ProtocolCommands.ERROR + ProtocolCommands.ARGUMENT_SEPARATOR + e);
                }
                break;
            case ProtocolCommands.PLAY_CARD:
                try {
                    explodingKittensServer.setPlayerResponseNotNeeded(this.getName());
                    explodingKittensServer.playCard(argument1, this.getName());
                } catch (E08 | E13 | E07 e) {
                    sendMessageToClient(ProtocolCommands.ERROR + ProtocolCommands.ARGUMENT_SEPARATOR + e);
                }
                break;
            case ProtocolCommands.DRAW_CARD:
                try {
                    explodingKittensServer.drawCard(this);
                } catch (E08 | E13 e) {
                    sendMessageToClient(ProtocolCommands.ERROR + ProtocolCommands.ARGUMENT_SEPARATOR + e);
                }
                break;
            case ProtocolCommands.STOP_SHUFFLE:
                try {
                    explodingKittensServer.handleResponseStopShuffle(this);
                } catch (E08 e) {
                    sendMessageToClient(ProtocolCommands.ERROR + ProtocolCommands.ARGUMENT_SEPARATOR + e);
                }
                break;
            case ProtocolCommands.RESPOND_YESORNO:
                try {
                    explodingKittensServer.handleResponseNopeCard(argument1, this);
                } catch (E13 | E08 | E07 e) {
                    sendMessageToClient(ProtocolCommands.ERROR + ProtocolCommands.ARGUMENT_SEPARATOR + e);
                }
                break;
            case ProtocolCommands.RESPOND_INDEX:
                try {
                    explodingKittensServer.handleResponseInsertExplodingKitten(argument1, this);
                } catch (E13 | E08 e) {
                    sendMessageToClient(ProtocolCommands.ERROR + ProtocolCommands.ARGUMENT_SEPARATOR + e);
                }
                break;
            case ProtocolCommands.RESPOND_PLAYERNAME:
                try {
                    explodingKittensServer.handleResponsePlayerName(argument1, this);
                } catch (E13 | E08 e) {
                    sendMessageToClient(ProtocolCommands.ERROR + ProtocolCommands.ARGUMENT_SEPARATOR + e);
                }
                break;
            case ProtocolCommands.RESPOND_CARDNAME:
                try {
                    explodingKittensServer.handleResponseCardName(argument1, this);
                } catch (E13 | E08 e) {
                    sendMessageToClient(ProtocolCommands.ERROR + ProtocolCommands.ARGUMENT_SEPARATOR + e);
                }
                break;
            case ProtocolCommands.SEND_MESSAGE:
                explodingKittensServer.sendMessageToPlayersChat(argument1, this);
                break;
        }
    }

    /**
     * This method is used by the ExplodingKittensServer to send a message to the client.
     * @param messageToSend the message to be sent
     */
    public synchronized void sendMessageToClient(String messageToSend) {
        try {
            bufferedWriter.write(messageToSend);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (IOException e) {
            System.out.println("Exception " + e);
        }
    }
}
