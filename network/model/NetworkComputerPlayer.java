package network.model;

import protocol.ProtocolCommands;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import static local.view.ANSI.*;

/**
 * Class to represent a network computer player in the Exploding Kittens game.
 * @author Alexandru-Cristian Enescu and Oliver Li
 */
public class NetworkComputerPlayer implements Runnable {
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private ArrayList<String> myCards;
    private String lastCardPlayed;
    private static int numberComputerPlayer = 0;
    private final String nameComputerPlayer;

    /**
     * Create a NetworkComputerPlayer.
     * Initialize the computer player's name.
     * Increase the variable which holds the number of computer players created by 1.
     */
    public NetworkComputerPlayer() {
        NetworkComputerPlayer.numberComputerPlayer += 1;
        this.nameComputerPlayer = "Computer Player " + NetworkComputerPlayer.numberComputerPlayer;
    }

    /**
     * Initialize the socket and connect to the server.
     * Initialize the buffered reader and the buffered writer.
     * Send to the server the first handshake message which includes the name of the computer player.
     */
    public void connectToServer() {
        try {
            this.socket = new Socket("localhost", 5000);
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            System.out.println("Exception " + e);
        }
        this.sendMessageToServer("CONNECT~" + this.nameComputerPlayer);
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
     * When a message is received, make a decision for the next move.
     */
    @Override
    public void run() {
        String messageFromServer;
        try {
            messageFromServer = bufferedReader.readLine();
            while (messageFromServer != null) {
                checkServerMessage(messageFromServer);
                System.out.println(BLUE_BOLD_BRIGHT + "Server -> " + this.nameComputerPlayer + ": " + RESET
                        + WHITE_BOLD + messageFromServer + RESET);
                messageFromServer = bufferedReader.readLine();
            }
        } catch (IOException e) {
            System.out.println("Exception " + e);
        }
    }

    /**
     * This method is called when a message from the server is received. It checks what command does the message have, and
     * for each case it either updates the computer player's cards or it calls other methods of the class to send a response.
     * @param message the message received from the server
     */
    public void checkServerMessage(String message) {
        String[] messageFromServer = message.split(ProtocolCommands.ARGUMENT_SEPARATOR);
        String command = messageFromServer[0];

        String argument1 = null;
        String argument2 = null;

        if(messageFromServer.length > 1) {
            argument1 = messageFromServer[1];
        }

        if(messageFromServer.length > 2) {
            argument2 = messageFromServer[2];
        }

        switch (command) {
            case ProtocolCommands.SHOW_HAND:
                if(argument1 == null) {
                    myCards = new ArrayList<>();
                } else {
                    myCards = new ArrayList<>(Arrays.asList(argument1.split(ProtocolCommands.ELEMENT_SEPARATOR)));
                }
                break;
            case ProtocolCommands.BROADCAST_MOVE:
                if(!argument1.equals(this.nameComputerPlayer)) {
                    if(argument2.contains(",")) {
                        String[] lastCardsPlayed = argument2.split(",");
                        lastCardPlayed = lastCardsPlayed[0];
                    } else {
                        lastCardPlayed = argument2;
                    }
                }
                break;
            case ProtocolCommands.CURRENT:
                if(argument1.equals(nameComputerPlayer)) {
                    playCard();
                }
                break;
            case ProtocolCommands.ASK_FOR_YESORNO:
                respondPlayNope();
                break;
            case ProtocolCommands.ASK_FOR_INDEX:
                respondInsertExplodingKitten(argument1);
                break;
            case ProtocolCommands.ASK_STOP_SHUFFLE:
                sendMessageToServer(ProtocolCommands.STOP_SHUFFLE);
                break;
            case ProtocolCommands.ASK_FOR_CARDNAME:
                respondCardName();
                break;
            case ProtocolCommands.ASK_FOR_PLAYERNAME:
                respondPlayerName(argument1);
                break;
            case ProtocolCommands.NEW_GAME:
                if(argument1.startsWith(this.nameComputerPlayer)) {
                    sendMessageToServer(chooseCardToPlay());
                }
                break;
            case ProtocolCommands.SHOW_FIRST_3_CARDS:
                this.checkFirst3Cards(argument1);
                break;
            case ProtocolCommands.GAME_OVER:
                if(argument1.equals(this.nameComputerPlayer)) {
                    sendMessageToServer(ProtocolCommands.SEND_MESSAGE + ProtocolCommands.ARGUMENT_SEPARATOR + "I won!");
                }
                break;
        }
    }

    /**
     * This method is used when the computer player has played a See The Future card. When the server sends a message with the
     * first 3 cards from the top of the draw pile, these are going to be checked by this method.
     * @param cards the first 3 cards from the top of the draw pile
     * @ensures if the first or third card from the draw pile is an Exploding Kitten, the computer player will try to play first a Skip
     * card or a Shuffle card, if it does not have any of these in its hand, it will choose other card using the method chooseCardToPlay().
     */
    private void checkFirst3Cards(String cards) {
        String messageToSend = ProtocolCommands.PLAY_CARD + ProtocolCommands.ARGUMENT_SEPARATOR;
        ArrayList<String> first3Cards = new ArrayList<>(Arrays.asList(cards.split(",")));

        if(first3Cards.get(0).equals("Exploding Kitten") || (first3Cards.get(2) != null && first3Cards.get(2).equals("Exploding Kitten"))) {
            if(this.checkMyCards("Skip")) {
                messageToSend += "Skip";
            } else if(this.checkMyCards("Shuffle")) {
                messageToSend += "Shuffle";
            } else {
                messageToSend = chooseCardToPlay();
            }
        } else {
            messageToSend = chooseCardToPlay();
        }
        sendMessageToServer(messageToSend);
    }

    /**
     * This method is used when it is the computer player's turn to play a card.
     * Firstly, a check is made to see if lastCardPlayed == null, in this case the computer player is the first player in the game.
     * The computer player tries to play an Attack card, and if it does not have it, the method chooseCardToPlay() is used to make the decision.
     */
    public void playCard() {
        String messageToSend = ProtocolCommands.PLAY_CARD + ProtocolCommands.ARGUMENT_SEPARATOR;

        if(lastCardPlayed == null) {
            sendMessageToServer(chooseCardToPlay());
            return;
        }

        if(lastCardPlayed.equalsIgnoreCase("Attack")) {
            if(this.checkMyCards("Attack")) {
                messageToSend += "Attack";
            } else {
                messageToSend = chooseCardToPlay();
            }
        } else {
            messageToSend = chooseCardToPlay();
        }
        sendMessageToServer(messageToSend);
    }

    /**
     * This method is called when the computer player must play a card. For each card which exists, a check is made to see
     * if the card is in the computer player's hand, such that it can be played.
     * @return a message to be sent to the server containing the card which will be played
     */
    private String chooseCardToPlay() {
        String messageToSend = ProtocolCommands.PLAY_CARD + ProtocolCommands.ARGUMENT_SEPARATOR;
        if(this.checkMyCards("Attack")) {
            messageToSend += "Attack";
        } else if(this.checkMyCards("Favor")) {
            messageToSend += "Favor";
        } else if(this.checkMyCardsForCombos("Beard Cat", 3)) {
            messageToSend += "Beard Cat,Beard Cat,Beard Cat";
        } else if(this.checkMyCardsForCombos("Cattermelon", 3)) {
            messageToSend += "Cattermelon,Cattermelon,Cattermelon";
        } else if(this.checkMyCardsForCombos("Rainbow Ralphing Cat", 3)) {
            messageToSend += "Rainbow Ralphing Cat,Rainbow Ralphing Cat,Rainbow Ralphing Cat";
        } else if(this.checkMyCardsForCombos("Hairy Potato Cat", 3)) {
            messageToSend += "Hairy Potato Cat,Hairy Potato Cat,Hairy Potato Cat";
        } else if(this.checkMyCardsForCombos("Taco Cat", 3)) {
            messageToSend += "Taco Cat,Taco Cat,Taco Cat";
        } else if(this.checkMyCardsForCombos("Beard Cat", 2)) {
            messageToSend += "Beard Cat,Beard Cat";
        } else if(this.checkMyCardsForCombos("Cattermelon", 2)) {
            messageToSend += "Cattermelon,Cattermelon";
        } else if(this.checkMyCardsForCombos("Rainbow Ralphing Cat", 2)) {
            messageToSend += "Rainbow Ralphing Cat,Rainbow Ralphing Cat";
        } else if(this.checkMyCardsForCombos("Hairy Potato Cat", 2)) {
            messageToSend += "Hairy Potato Cat,Hairy Potato Cat";
        } else if(this.checkMyCardsForCombos("Taco Cat", 2)) {
            messageToSend += "Taco Cat,Taco Cat";
        } else if(this.checkMyCards("See The Future")) {
            messageToSend += "See The Future";
        } else {
            messageToSend = ProtocolCommands.DRAW_CARD;
        }
        return messageToSend;
    }

    public void respondPlayNope() {
        String messageToSend = ProtocolCommands.RESPOND_YESORNO + ProtocolCommands.ARGUMENT_SEPARATOR;
        if(this.checkMyCards("Nope")) {
            messageToSend += "YES";
        } else {
            messageToSend += "NO";
        }
        sendMessageToServer(messageToSend);
    }

    public void respondInsertExplodingKitten(String deckSize) {
        sendMessageToServer(ProtocolCommands.RESPOND_INDEX + ProtocolCommands.ARGUMENT_SEPARATOR + deckSize);
    }

    public void respondCardName() {
        String messageToSend = ProtocolCommands.RESPOND_CARDNAME + ProtocolCommands.ARGUMENT_SEPARATOR;
        if(this.lastCardPlayed.equalsIgnoreCase("Favor")) {
            messageToSend += chooseCardToGive();
        } else {
            messageToSend += "Defuse";
        }
        sendMessageToServer(messageToSend);
    }

    public void respondPlayerName(String playerNames) {
        String[] players = playerNames.split(",");
        String playerToStealCardFrom = nameComputerPlayer;
        while(playerToStealCardFrom.equals(nameComputerPlayer)) {
            playerToStealCardFrom = players[(int) (Math.random() * players.length)];
        }
        sendMessageToServer(ProtocolCommands.RESPOND_PLAYERNAME + ProtocolCommands.ARGUMENT_SEPARATOR + playerToStealCardFrom);
    }

    private String chooseCardToGive() {
        String cardToGive = "";
        if(this.checkMyCards("Hairy Potato Cat")) {
            cardToGive = "Hairy Potato Cat";
        } else if(this.checkMyCards("Cattermelon")) {
            cardToGive = "Cattermelon";
        } else if(this.checkMyCards("Beard Cat")) {
            cardToGive = "Beard Cat";
        } else if(this.checkMyCards("Rainbow Ralphing Cat")) {
            cardToGive = "Rainbow Ralphing Cat";
        } else if(this.checkMyCards("Taco Cat")) {
            cardToGive = "Taco Cat";
        } else if(this.checkMyCards("Shuffle")) {
            cardToGive = "Shuffle";
        } else if(this.checkMyCards("Favor")) {
            cardToGive = "Favor";
        } else if(this.checkMyCards("See The Future")) {
            cardToGive = "See The Future";
        } else if(this.checkMyCards("Skip")) {
            cardToGive = "Skip";
        } else if(this.checkMyCards("Nope")) {
            cardToGive = "Nope";
        } else if(this.checkMyCards("Attack")) {
            cardToGive = "Attack";
        } else if(this.checkMyCards("Defuse")) {
            cardToGive = "Defuse";
        }
        return cardToGive;
    }

    public boolean checkMyCardsForCombos(String cardTypeToCheck, int numberOfCardsNeeded) {
        int numberOfCardsIhave = 0;
        for(String card : this.myCards) {
            if(card.contains(cardTypeToCheck)) {
                numberOfCardsIhave += 1;
            }
            if(numberOfCardsIhave == numberOfCardsNeeded) {
                return true;
            }
        }
        return false;
    }

    public boolean checkMyCards(String cardTypeToCheck) {
        for(String card : myCards) {
            if(card.contains(cardTypeToCheck)) {
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        NetworkComputerPlayer networkComputerPlayer = new NetworkComputerPlayer();
        networkComputerPlayer.connectToServer();
        Thread thread = new Thread(networkComputerPlayer);
        thread.start();
    }
}
