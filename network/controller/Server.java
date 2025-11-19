package network.controller;

import exceptions.*;
import local.model.CardType;
import local.model.Player;
import network.model.NetworkComputerPlayer;
import network.model.NetworkGame;
import network.model.ShuffleDeck;
import protocol.ProtocolCommands;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Class to represent the Server which controls the Exploding Kittens game.
 * @author Alexandru-Cristian Enescu and Oliver Li
 */
public class Server {
    private ServerSocket serverSocket;
    private List<ClientHandler> clientHandlerList;
    private NetworkGame game;
    private boolean favorCardPlayed;
    private ClientHandler clientHandlerToStealCardFrom;
    private ArrayList<String> comboCards;
    private boolean comboTwoCardsPlayed;
    private boolean comboThreeCardsPlayed;
    private ArrayList<Boolean> playersResponsesAgreeContinueGame;
    private boolean stopLastAction;
    private String cardPlayedBeforeNope;
    private boolean continueGame;
    private String playerResponseNotNeeded;
    private ArrayList<String> firstPlayerFlags;
    private boolean chatActive;
    private boolean specialCombosActive;

    /**
     * Create an Exploding Kittens server, initialize the clientHandlersList.
     * Set the instance variables which keep track of the state of the game to an initial value.
     */
    public Server() {
        this.clientHandlerList = new ArrayList<>();
        this.favorCardPlayed = false;
        this.clientHandlerToStealCardFrom = null;
        this.comboCards = null;
        this.comboTwoCardsPlayed = false;
        this.comboThreeCardsPlayed = false;
        this.stopLastAction = false;
        this.continueGame = false;
        this.chatActive = false;
        this.specialCombosActive = false;
    }

    /**
     * Change the player name who will not be asked to play a Nope card. His response is not needed because he played the card before the Nope.
     * @param playerResponseNotNeeded the name of the player who is not asked to play a Nope card
     * @requires playerResponseNotNeeded != null
     */
    public synchronized void setPlayerResponseNotNeeded(String playerResponseNotNeeded) {
        this.playerResponseNotNeeded = playerResponseNotNeeded;
    }

    /**
     * Get the names of all connected players.
     * @return a String containing the names of all connected players separated by ","
     */
    public synchronized String getConnectedPlayersNames() {
        ArrayList<String> connectedPlayersNames = new ArrayList<>();
        for(ClientHandler clientHandler : clientHandlerList) {
            if(clientHandler.getName() != null) {
                connectedPlayersNames.add(clientHandler.getName());
            }
        }

        String result = "";
        for(String playerName : connectedPlayersNames) {
            if(connectedPlayersNames.indexOf(playerName) != connectedPlayersNames.size() - 1) {
                result += playerName + ",";
            } else {
                result += playerName;
            }
        }
        return result;
    }

    /**
     * Get the number of connected players.
     * @return an integer which represents the number of connected players
     */
    public synchronized int getNumberConnectedPlayers() {
        String[] connectedPlayers = this.getConnectedPlayersNames().split(",");
        return connectedPlayers.length;
    }

    /**
     * Initialize the ServerSocket.
     */
    public void startServer() {
        try {
            serverSocket = new ServerSocket(5000);
        } catch (IOException e) {
            System.out.println("Exception " + e);
        }
    }

    /**
     * Listen for connections and create a new ClientHandler thread for each connected player.
     * Add the new ClientHandler to clientHandlerList.
     */
    public void listenForConnections() {
        try {
            while(true) {
                Socket socket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(socket, this);
                clientHandlerList.add(clientHandler);
                Thread thread = new Thread(clientHandler);
                thread.start();
            }
        } catch (IOException e) {
            System.out.println("Exception " + e);
        }
    }

    /**
     * Send a message only to one player.
     * @param messageToSend the message to be sent
     * @requires messageToSend != null, messageReceiver != null
     */
    public synchronized void sendMessageToOnePlayer(String messageToSend, ClientHandler messageReceiver) {
        for(ClientHandler clientHandler : clientHandlerList) {
            if(clientHandler.equals(messageReceiver)) {
                clientHandler.sendMessageToClient(messageToSend);
            }
        }
    }

    /**
     * Send a message to all clients, including the sender of the message.
     * @param messageToSend the message to be sent
     * @requires messageToSend != null
     */
    public synchronized void sendMessageToAllPlayers(String messageToSend) {
        for(ClientHandler clientHandler : clientHandlerList) {
            clientHandler.sendMessageToClient(messageToSend);
        }
    }

    /**
     * Send a message to all clients, except the sender of the message.
     * @param messageToSend the message to be sent
     * @param playerWhoSentMessage the player who has sent the message and will not receive it
     * @requires messageToSend != null, playerWhoSentMessage != null
     */
    public synchronized void sendMessageToPlayersChat(String messageToSend, ClientHandler playerWhoSentMessage) {
        if(this.chatActive) {
            for (ClientHandler clientHandler : clientHandlerList) {
                if(!clientHandler.equals(playerWhoSentMessage)) {
                    clientHandler.sendMessageToClient(ProtocolCommands.SHOW_MESSAGE + ProtocolCommands.ARGUMENT_SEPARATOR + playerWhoSentMessage.getName() + ProtocolCommands.ARGUMENT_SEPARATOR + messageToSend);
                }
            }
        }
    }

    /**
     * Handle the handshake. If the player who connected entered flags whose corresponding features are available on server,
     * enable those features. Respond the player with a Hello message, including the flags available on the server.
     * @param clientHandler the player who has connected to the server
     * @param flags the flags entered by the player who connected
     * @throws E09 if a player has connected with different flags than the first connected player
     */
    public synchronized void sendHello(ClientHandler clientHandler, String flags) throws E09 {
        if(flags != null) {
            if(this.getNumberConnectedPlayers() == 1) {
                this.firstPlayerFlags = new ArrayList<>(Arrays.asList(flags.split(",")));
                if(firstPlayerFlags.contains("0")) {
                    this.chatActive = true;
                }
                if(firstPlayerFlags.contains("4")) {
                    this.specialCombosActive = true;
                }
            } else if(this.firstPlayerFlags != null){
                ArrayList<String> newPlayerFlags = new ArrayList<>(Arrays.asList(flags.split(",")));
                for (String newFlag : newPlayerFlags) {
                    if(!firstPlayerFlags.contains(newFlag)) {
                        throw new E09();
                    }
                }
            }
        }

        sendMessageToOnePlayer(ProtocolCommands.HELLO + ProtocolCommands.ARGUMENT_SEPARATOR + clientHandler.getName() + ProtocolCommands.ARGUMENT_SEPARATOR + "0,3,4", clientHandler);
        this.sendMessageToAllPlayers(this.sendPlayerList());
        this.sendMessageToAllPlayers(this.sendQueueLength());
    }

    /**
     * Get the list of connected players.
     * @return a String which contains the list of connected players
     */
    public synchronized String sendPlayerList() {
        return ProtocolCommands.PLAYER_LIST + ProtocolCommands.ARGUMENT_SEPARATOR + this.getConnectedPlayersNames();
    }

    /**
     * Get the number of how many players are in the lobby.
     * @return a String containing the number of players waiting in the lobby
     */
    public synchronized String sendQueueLength() {
        return ProtocolCommands.QUEUE + ProtocolCommands.ARGUMENT_SEPARATOR + this.getNumberConnectedPlayers();
    }

    /**
     * Create a new NetworkComputerPlayer and connect it to the server.
     */
    public synchronized void addComputerPlayer() {
        NetworkComputerPlayer networkComputerPlayer = new NetworkComputerPlayer();
        networkComputerPlayer.connectToServer();
        Thread thread = new Thread(networkComputerPlayer);
        thread.start();
    }

    /**
     * Remove one NetworkComputerPlayer from the server.
     * @throws E06 if this method is called and there are no computer players connected
     */
    public synchronized void removeComputerPlayer() throws E06 {
        if(checkComputerPlayersConnected()) {
            for(ClientHandler clientHandler : this.clientHandlerList) {
                if(clientHandler.getName().startsWith("Computer")) {
                    this.clientHandlerList.remove(clientHandler);
                    clientHandler.setSocket(null);
                    clientHandler.setBufferedReader(null);
                    clientHandler.setBufferedWriter(null);
                    break;
                }
            }
        } else {
            throw new E06();
        }
        this.sendMessageToAllPlayers(this.sendPlayerList());
        this.sendMessageToAllPlayers(this.sendQueueLength());
    }

    /**
     * Check if there is any NetworkComputerPlayer object connected to the server.
     * @return true if there is at least one NetworkComputerPlayer connected, false otherwise
     */
    public synchronized boolean checkComputerPlayersConnected() {
        for(ClientHandler clientHandler : this.clientHandlerList) {
            if(clientHandler.getName().startsWith("Computer")) {
                return true;
            }
        }
        return false;
    }

    /**
     * Create a new NetworkGame and start it.
     * @param numberOfPlayers the number of players for which the game will be made
     * @param clientHandler the ClientHandler who started the game
     * @requires numberOfPlayers != null, clientHandler != null
     */
    public synchronized void startNewGame(String numberOfPlayers, ClientHandler clientHandler) throws E05, NumberFormatException, E08, E13, E11 {
        if(clientHandler.getName() == null) {
            throw new E08();
        }

        if(game != null && !game.gameOver()) {
            throw new E08();
        }

        int numberPlayers = Integer.parseInt(numberOfPlayers);

        if(numberPlayers == 1) {
            throw new E05();
        } else if(numberPlayers == 2 && (clientHandlerList.size() < 2)) {
            throw new E05();
        } else if(numberPlayers == 3 && (clientHandlerList.size() < 3)) {
            throw new E05();
        } else if(numberPlayers == 4 && (clientHandlerList.size() < 4)) {
            throw new E05();
        } else if(numberPlayers == 5 && (clientHandlerList.size() < 5)) {
            throw new E05();
        } else if(numberPlayers > 5) {
            throw new E11();
        }

        ArrayList<String> playersNames = new ArrayList<>();
        for(ClientHandler player : clientHandlerList) {
            playersNames.add(player.getName());
        }

        if(checkNumberComputerPlayersConnected() == numberPlayers) {
            for(int i=0; i<playersNames.size(); i++) {
                if(!playersNames.get(i).startsWith("Computer")) {
                    playersNames.remove(playersNames.get(i));
                    i--;
                }
            }
            for(int i=0; i<clientHandlerList.size(); i++) {
                if(!clientHandlerList.get(i).getName().startsWith("Computer")) {
                    this.clientHandlerList.remove(i);
                    i--;
                }
            }
        }

        this.game = new NetworkGame(playersNames);
        this.game.setUpGame();

        this.playersResponsesAgreeContinueGame = new ArrayList<>();
        for(Player player : game.getPlayers()) {
            playersResponsesAgreeContinueGame.add(false);
        }

        this.playerResponseNotNeeded = game.getCurrentPlayer().getName();
        this.sendEachPlayerHand();
        this.announceCurrentPlayer();
        this.sendMessageToAllPlayers(ProtocolCommands.NEW_GAME + ProtocolCommands.ARGUMENT_SEPARATOR + game.getPlayersNames());
    }

    /**
     * Check how many NetworkComputerPlayer objects are connected to the server.
     * @return an integer which represents the number of computer players connected
     */
    public synchronized int checkNumberComputerPlayersConnected() {
        int count = 0;
        for(ClientHandler clientHandler : clientHandlerList) {
            if(clientHandler.getName() != null && clientHandler.getName().startsWith("Computer")) {
                count += 1;
            }
        }
        return count;
    }

    /**
     * Send to each player his hand of cards.
     */
    public synchronized void sendEachPlayerHand() {
        for(ClientHandler clientHandler : clientHandlerList) {
            for(Player player : game.getPlayers()) {
                if(clientHandler.getName().equals(player.getName())) {
                    clientHandler.sendMessageToClient(ProtocolCommands.SHOW_HAND + ProtocolCommands.ARGUMENT_SEPARATOR + player.getPlayerHandString());
                }
            }
        }
    }

    /**
     * Send the hand of cards to a specific player.
     * @param playerName the name of the player to which the message is sent
     * @requires playerName != null
     */
    public synchronized void sendPlayerHand(String playerName) throws E13 {
        for(ClientHandler clientHandler : clientHandlerList) {
            if(clientHandler.getName().equals(playerName)) {
                clientHandler.sendMessageToClient(ProtocolCommands.SHOW_HAND + ProtocolCommands.ARGUMENT_SEPARATOR + this.getPlayerByName(playerName).getPlayerHandString());
            }
        }
    }

    /**
     * This method is called when the current player plays a card or other player plays a Nope card.
     * @param playedCard the name of the card which has been played
     * @param playerName the name of the player who has played the card
     * @throws E08 if a player wants to play a card (which is not a Nope card) when it is not his turn
     */
    public synchronized void playCard(String playedCard, String playerName) throws E08, E13, E07 {
        // check if the player who plays the card is the current player. It can be another player only if a Nope card is played
        if(!game.getCurrentPlayer().getName().equals(playerName) && !playedCard.equalsIgnoreCase("NOPE")) {
            throw new E08();
        }

        if(playedCard.contains(",") && !game.hasCards(playerName, playedCard)) {
            throw new E07();
        }

        if(!playedCard.contains(",") && !game.hasCard(playerName, playedCard)) {
            throw new E07();
        }

        if    ((playedCard.equalsIgnoreCase("Rainbow Ralphing Cat") ||
                playedCard.equalsIgnoreCase("Hairy Potato Cat") ||
                playedCard.equalsIgnoreCase("Taco Cat") ||
                playedCard.equalsIgnoreCase("Beard Cat") ||
                playedCard.equalsIgnoreCase("Cattermelon")) &&
                !playedCard.contains(",")) {
            throw new E13();
        }

        if(playedCard.contains("Defuse")) {
            throw new E13();
        }

        if(playedCard.contains(",")) {
            this.comboCards = new ArrayList<>(Arrays.asList(playedCard.split(",")));
            game.checkCombo(this.comboCards, this.specialCombosActive);
        }

        // if a Nope card was played, update the variable "stopLastAction", discard the Nope card from player's hand, show player his hand of cards
        if(playedCard.equalsIgnoreCase("NOPE")) {
            this.stopLastAction = !this.stopLastAction;
            this.game.playNopeCard(playerName);
            this.sendPlayerHand(playerName);
            this.playerResponseNotNeeded = playerName;
            sendMessageToAllPlayers(ProtocolCommands.BROADCAST_MOVE + ProtocolCommands.ARGUMENT_SEPARATOR + playerName + ProtocolCommands.ARGUMENT_SEPARATOR + playedCard);

            // if another card except the Nope card was played, update variable "cardPlayedBeforeNope"
        } else {
            this.cardPlayedBeforeNope = playedCard;
            if(!this.stopLastAction) {
                sendMessageToAllPlayers(ProtocolCommands.BROADCAST_MOVE + ProtocolCommands.ARGUMENT_SEPARATOR + playerName + ProtocolCommands.ARGUMENT_SEPARATOR + playedCard);
            }
        }

        // check if everybody refused to play a Nope card or if nobody has Nope cards in their hands
        if(this.checkNobodyPlaysNopeCard() || game.noNopeCardsAtPlayers()) {
            this.continueGame = true;

            // if a Nope card was played, update the variable "playedCard"
            // when the game continues, the card whose action was stopped by the Nope card will be played if necessary
            if(playedCard.equalsIgnoreCase("NOPE")) {
                playedCard = this.cardPlayedBeforeNope;
            }
        } else {

            // ask each player if they want to play a Nope card, except the player who played the card before a Nope
            // ask only if a Nope card is in the player's hand and the player didn't refuse to play a Nope card yet
            for(ClientHandler clientHandler : this.clientHandlerList) {
                if(game.getPlayersNames().contains(clientHandler.getName())) {
                    if(game.hasCard(clientHandler.getName(), "Nope") && !this.playersResponsesAgreeContinueGame.get(this.clientHandlerList.indexOf(clientHandler)) && !clientHandler.getName().equals(playerName) && !clientHandler.getName().equals(this.playerResponseNotNeeded)) {
                        sendMessageToOnePlayer(ProtocolCommands.ASK_FOR_YESORNO, clientHandler);
                    }
                }
            }
        }

        // the game can continue only when all players refused to play the Nope card or when nobody has Nope cards in their hands
        if(this.continueGame) {

            // if the last Nope card which was played created a Yup, the card whose action was stopped will be played
            if(!this.stopLastAction) {
                if(playedCard.contains(",")) {
                    sendMessageToOnePlayer(ProtocolCommands.ASK_FOR_PLAYERNAME + ProtocolCommands.ARGUMENT_SEPARATOR + this.getAllPlayersExceptCurrentPlayer(), this.getClientHandlerByName(playerName));

                    if(comboCards.size() == 2) {
                        this.comboTwoCardsPlayed = true;
                        game.discardCards(comboCards.get(0), 2);
                    } else if(comboCards.size() == 3) {
                        this.comboThreeCardsPlayed = true;
                        game.discardCards(comboCards.get(0), 3);
                    }
                } else {
                    switch (playedCard.toUpperCase()) {
                        case "SKIP":
                            game.playSkipCard();
                            break;
                        case "SHUFFLE":
                            game.discardCard("Shuffle", playerName);
                            sendMessageToOnePlayer(ProtocolCommands.ASK_STOP_SHUFFLE, getClientHandlerByName(game.getPlayers().get(game.getNextPlayerIndex()).getName()));
                            game.setKeepShuffle(true);
                            ShuffleDeck shuffleDeck = new ShuffleDeck(game);
                            shuffleDeck.start();
                            break;
                        case "ATTACK":
                            game.discardCard("Attack", playerName);
                            this.sendPlayerHand(game.getCurrentPlayer().getName());
                            game.playAttackCard();
                            break;
                        case "FAVOR":
                            this.sendMessageToOnePlayer(ProtocolCommands.ASK_FOR_PLAYERNAME + ProtocolCommands.ARGUMENT_SEPARATOR + this.getAllPlayersExceptCurrentPlayer(), this.getClientHandlerByName(playerName));
                            this.favorCardPlayed = true;
                            break;
                        case "SEE THE FUTURE":
                            this.sendMessageToOnePlayer(ProtocolCommands.SHOW_FIRST_3_CARDS + ProtocolCommands.ARGUMENT_SEPARATOR + game.playSeeTheFutureCard(), this.getClientHandlerByName(game.getCurrentPlayer().getName()));
                            break;
                    }
                }
            } else {
                if(playedCard.contains(",")) {
                    game.discardCards(comboCards.get(0), this.comboCards.size());
                } else {
                    game.discardCard(playedCard, game.getCurrentPlayer().getName());
                }
            }
            this.stopLastAction = false;

            if(!this.favorCardPlayed && !this.comboTwoCardsPlayed && !this.comboThreeCardsPlayed) {
                this.sendPlayerHand(game.getCurrentPlayer().getName());
                announceCurrentPlayer();
            }

            this.playersResponsesAgreeContinueGame = new ArrayList<>();
            for(int i=0; i<game.getPlayers().size(); i++) {
                playersResponsesAgreeContinueGame.add(false);
            }

            this.continueGame = false;
        }
    }

    /**
     * This method is called when the current player draws a card.
     * @param player the player who draws a card
     * @throws E08 if the player who draws a card is not the current player
     * @requires player != null
     */
    public synchronized void drawCard(ClientHandler player) throws E08, E13 {
        if(!game.getCurrentPlayer().getName().equals(player.getName())) {
            throw new E08();
        }
        if(game.drawCard(game.getCurrentPlayer()).getCardType().equals(CardType.EXPLODING_KITTEN)) {
            sendMessageToAllPlayers(ProtocolCommands.EXPLODING_KITTEN + ProtocolCommands.ARGUMENT_SEPARATOR + game.getCurrentPlayer().getName());
            if(game.checkForDefuseCard()) {
                sendMessageToOnePlayer(ProtocolCommands.ASK_FOR_INDEX + ProtocolCommands.ARGUMENT_SEPARATOR +
                        game.getDeck().getDrawPile().size(), this.getClientHandlerByName(game.getCurrentPlayer().getName()));
            } else {
                sendMessageToAllPlayers(ProtocolCommands.PLAYER_OUT + ProtocolCommands.ARGUMENT_SEPARATOR + game.getCurrentPlayer().getName());
                game.playExplodingKittenCard();
                if(game.gameOver()) {
                    sendMessageToAllPlayers(ProtocolCommands.GAME_OVER + ProtocolCommands.ARGUMENT_SEPARATOR + game.getCurrentPlayer().getName());
                } else {
                    this.playersResponsesAgreeContinueGame = new ArrayList<>();
                    for(int i=0; i<game.getPlayers().size(); i++) {
                        playersResponsesAgreeContinueGame.add(false);
                    }
                    this.announceCurrentPlayer();
                }
            }
            return;
        }
        sendMessageToAllPlayers(ProtocolCommands.BROADCAST_MOVE + ProtocolCommands.ARGUMENT_SEPARATOR + player.getName() + ProtocolCommands.ARGUMENT_SEPARATOR + ProtocolCommands.DRAW_CARD);
        this.sendPlayerHand(player.getName());
        game.changeTurnToNextPlayer();
        game.checkAttackOn();
        this.announceCurrentPlayer();
    }

    /**
     * Send a message to all players to announce who is the current player of the game.
     */
    public synchronized void announceCurrentPlayer() {
        this.sendMessageToAllPlayers(ProtocolCommands.CURRENT + ProtocolCommands.ARGUMENT_SEPARATOR + game.getCurrentPlayer());
    }

    /**
     * When this method is called, the deck will not be shuffled anymore.
     * @param clientHandler the player who stopped the deck shuffling
     * @requires clientHandler != null
     * @throws E08 if clientHandler is not the next player after the current player
     */
    public synchronized void handleResponseStopShuffle(ClientHandler clientHandler) throws E08 {
        if(!game.getPlayers().get(game.getNextPlayerIndex()).getName().equals(clientHandler.getName())) {
            throw new E08();
        }
        this.game.setKeepShuffle(false);
    }

    /**
     * This method is called when a player answers to the question if they want to play a Nope card.
     * @param response the player's response
     * @param clientHandler the player who sent the response
     * @requires response != null, clientHandler != null
     */
    public synchronized void handleResponseNopeCard(String response, ClientHandler clientHandler) throws E13, E08, E07 {
        if(!game.hasCard(clientHandler.getName(), "Nope") || this.playersResponsesAgreeContinueGame.get(this.clientHandlerList.indexOf(clientHandler)) || clientHandler.getName().equals(this.playerResponseNotNeeded)) {
            throw new E08();
        }

        if(response.equalsIgnoreCase("NO")) {
            this.playersResponsesAgreeContinueGame.set(clientHandlerList.indexOf(clientHandler), true);
            if(this.checkNobodyPlaysNopeCard() || game.noNopeCardsAtPlayers()) {
                this.continueGame = true;
                playCard(this.cardPlayedBeforeNope, game.getCurrentPlayer().getName());
                this.stopLastAction = false;
            }
        } else {
            playCard("Nope", clientHandler.getName());
        }
    }

    /**
     * This method is called when the player who played a Defuse card answers with the index where he wants to insert the Exploding Kitten card.
     * @param response the index where the player wants to insert the Exploding Kitten card
     * @param clientHandler the player who sent the index
     * @requires response != null, clientHandler != null
     * @throws E08 if the player who sent the index is not the current player
     */
    public synchronized void handleResponseInsertExplodingKitten(String response, ClientHandler clientHandler) throws E13, E08 {
        if(!game.getCurrentPlayer().getName().equals(clientHandler.getName())) {
            throw new E08();
        }

        try {
            game.playDefuseCard(response);
            this.sendPlayerHand(clientHandler.getName());
            game.changeTurnToNextPlayer();
            game.checkAttackOn();
            this.announceCurrentPlayer();
        } catch (NumberFormatException e) {
            sendMessageToOnePlayer(ProtocolCommands.ERROR + ProtocolCommands.ARGUMENT_SEPARATOR + e, this.getClientHandlerByName(game.getCurrentPlayer().getName()));
        }
    }

    /**
     * This method is called when a Favor card is played or 2 / 3 cards in combo are played and the current player answers with
     * the name of the player from which he wants to take a card.
     * @param response the name of the player from which the current player wants to take a card
     * @param clientHandler the player who sent the message
     * @requires response != null, clientHandler != null
     * @throws E08 if the player who sent the message is not the current player
     */
    public synchronized void handleResponsePlayerName(String response, ClientHandler clientHandler) throws E13, E08 {
        if(!game.getCurrentPlayer().getName().equals(clientHandler.getName())) {
            throw new E08();
        }

        if(favorCardPlayed) {
            this.clientHandlerToStealCardFrom = this.getClientHandlerByName(response);
            sendMessageToOnePlayer(ProtocolCommands.ASK_FOR_CARDNAME, clientHandlerToStealCardFrom);
        } else if(comboTwoCardsPlayed) {
            game.playSpecialComboTwoCards(this.getPlayerByName(response));
            this.sendEachPlayerHand();
            this.announceCurrentPlayer();
            this.comboTwoCardsPlayed = false;
        } else if(comboThreeCardsPlayed) {
                this.clientHandlerToStealCardFrom = this.getClientHandlerByName(response);
                sendMessageToOnePlayer(ProtocolCommands.ASK_FOR_CARDNAME, getClientHandlerByName(game.getCurrentPlayer().getName()));
        }
    }

    /**
     * This method is called when a Favor card and the player who must give a card responds with the card name.
     * This method is also used when 3 cards are played in combo and the current player answers with the card he wishes
     * to take from another player.
     * @param response the name of the card
     * @param clientHandler the player who sent the message
     * @requires response != null, clientHandler != null
     * @throws E13 if a Favor card was played and the player who must give the current player a card does not have in his hand the entered card
     * @throws E08 if a Favor card was played and the player who sent the message is not the player from which the current player
     *             wants to steal a card
     * @throws E08 if 3 cards are played in combo and the player who sent the message is not the current player
     */
    public synchronized void handleResponseCardName(String response, ClientHandler clientHandler) throws E13, E08 {
        if(favorCardPlayed) {
            if(!this.clientHandlerToStealCardFrom.getName().equals(clientHandler.getName())) {
                throw new E08();
            }
            Player player = this.getPlayerByName(this.clientHandlerToStealCardFrom.getName());
            if(player.getPlayerHandString().toLowerCase().contains(response.toLowerCase())) {
                game.playFavorCard(player, response);
                sendEachPlayerHand();
                favorCardPlayed = false;
                clientHandlerToStealCardFrom = null;
                announceCurrentPlayer();
            }
            if(favorCardPlayed) {
                throw new E13();
            }
        } else if(comboThreeCardsPlayed) {
            if(!game.getCurrentPlayer().getName().equals(clientHandler.getName())) {
                throw new E08();
            }
            game.playSpecialComboThreeCards(this.getPlayerByName(this.clientHandlerToStealCardFrom.getName()), response);
            this.sendEachPlayerHand();
            this.announceCurrentPlayer();
            this.comboThreeCardsPlayed = false;
            this.clientHandlerToStealCardFrom = null;
        }
    }

    /**
     * Get the Player object by entering his name.
     * @param playerName the name of the player
     * @requires playerName != null
     * @return a Player object whose name is equal to playerName
     * @throws E13 if there is no player whose name is equal to playerName
     */
    public synchronized Player getPlayerByName(String playerName) throws E13 {
        for(Player player : game.getPlayers()) {
            if(player.getName().equals(playerName)) {
                return player;
            }
        }
        throw new E13();
    }

    /**
     * Check if all players have responded with "no" to the question if they want to play a Nope card.
     * @return true if nobody wants to play a Nope card, false otherwise
     */
    public synchronized boolean checkNobodyPlaysNopeCard() {
        for(int i = 0; i<this.playersResponsesAgreeContinueGame.size(); i++) {
            if(!this.playersResponsesAgreeContinueGame.get(i) &&
                    game.hasCard(game.getPlayers().get(i).getName(), "Nope") &&
                    !game.getPlayers().get(i).getName().equals(this.playerResponseNotNeeded)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Get a ClientHandler by entering his name.
     * @param clientHandlerName the name of the ClientHandler
     * @requires clientHandlerName != null
     * @return a ClientHandler object whose name is equal to clientHandlerName
     * @throws E13 if there is no ClientHandler whose name is equal to clientHandlerName
     */
    public synchronized ClientHandler getClientHandlerByName(String clientHandlerName) throws E13 {
        for(ClientHandler clientHandler : this.clientHandlerList) {
            if(clientHandler.getName().equals(clientHandlerName)) {
                return clientHandler;
            }
        }
        throw new E13();
    }

    /**
     * Get all players of the game, except the current player.
     * This method is used when the current player plays a Favor card, and he must receive a message with all players names.
     * @return a String which contains the names of all players, except the current player
     */
    public synchronized String getAllPlayersExceptCurrentPlayer() {
        String result = "";
        for(int i=0; i<game.getPlayers().size(); i++) {
            if(!game.getCurrentPlayer().getName().equals(game.getPlayers().get(i).getName())) {
                if(i != game.getPlayers().size() - 1) {
                    result += game.getPlayers().get(i).getName() + ",";
                } else {
                    result += game.getPlayers().get(i).getName();
                }
            }
        }
        return result;
    }

    public static void main(String[] args) {
        Server explodingKittensServer = new Server();
        explodingKittensServer.startServer();
        explodingKittensServer.listenForConnections();
    }
}
