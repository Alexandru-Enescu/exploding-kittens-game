package network.view;

import local.model.CardType;
import network.model.NetworkPlayer;
import java.util.Scanner;

import static local.view.ANSI.*;
import static protocol.ProtocolCommands.*;

/**
 * Class to represent the TUI for the NetworkPlayer.
 * @author Oliver Li and Alexandru-Cristian Enescu
 */
public class NetworkTUI {
    private final NetworkPlayer NETWORK_PLAYER;
    private static final String COMMAND_LINE = YELLOW_BOLD + """
            You can use the Standard commands on the left or the Custom commands on the right.
            Standard commands ................... Custom commands
            CONNECT~Name~0,1         .....        connect-oliver-0,1
            ADD_COMPUTER             .....        add computer
            REMOVE_COMPUTER          .....        remove computer
            REQUEST_GAME~2           .....        request game-2
            PLAY_CARD~favor          .....        play-favor
            DRAW_CARD                .....        draw
            SEND~hello world         .....        send-hello world
            RESPOND_YESORNO~yes      .....        respond nope-yes
            RESPOND_INDEX~10         .....        respond index-10
            RESPOND_PLAYERNAME~Alex  .....        respond name-Alex
            RESPOND_CARDNAME~Defuse  .....        respond card-Defuse
            STOP_SHUFFLE             .....        stop
            """ + RESET;

    /**
     * Create a NetworkTUI and initialize its NetworkPlayer attribute.
     * @param networkPlayer the NetworkPlayer for whom this TUI is created
     */
    public NetworkTUI(NetworkPlayer networkPlayer) {
        this.NETWORK_PLAYER = networkPlayer;
    }

    /**
     * This method is used to translate the input from the player to messages which are sent to the server.
     */
    public void getPlayerInput() {
        Scanner scanner = new Scanner(System.in);
        String playerInput;
        String unknownCommand = "Unknown Command, please enter again. Type COMMAND to view all available commands.";

        while(scanner.hasNextLine()) {
            playerInput = scanner.nextLine();
            String result = "";

            //if player input has ~ , use Standard Command Line
            if (playerInput.contains(ARGUMENT_SEPARATOR)){
                String[] commandArray = playerInput.split(ARGUMENT_SEPARATOR);
                String command = commandArray[0];

                switch (command.toUpperCase()){
                    case CONNECT_TO_SERVER ->{
                        if (commandArray.length>2){
                            String playerName = commandArray[1];
                            String flags = commandArray[2];
                            if (playerName.isEmpty()){
                                System.out.println("Please enter player name to start connect.");
                            }else {
                                result = CONNECT_TO_SERVER+ARGUMENT_SEPARATOR+playerName+ARGUMENT_SEPARATOR+flags;
                            }
                        } else if (commandArray.length > 1) {
                            String playerName = commandArray[1];
                            if (playerName.isEmpty()){
                                System.out.println("Please enter player name to start connect.");
                            }else {
                                result = CONNECT_TO_SERVER+ARGUMENT_SEPARATOR+playerName;
                            }
                        }else {
                            System.out.println("Please enter player name to start connect.");
                        }
                    }
                    case ADD_COMPUTER_PLAYER ->{
                        System.out.println(YELLOW_BOLD + "\uD83E\uDD16 Welcome, Computer Whiz! \uD83D\uDCBE Someone just plugged in some digital magic. Ready for the techy vibes?" + RESET);
                        result = ADD_COMPUTER_PLAYER;
                    }
                    case REMOVE_COMPUTER_PLAYER ->{
                        System.out.println(YELLOW_BOLD + "\uD83D\uDEA8 Uh-oh, the computer is taking a coffee break! ☕ Adiós, Computer Buddy!" + RESET);
                        result = REMOVE_COMPUTER_PLAYER;
                    }
                    case REQUEST_GAME ->{
                        if (commandArray.length > 1){
                            String size = commandArray[1];
                            if (size.isEmpty()){
                                System.out.println("Please enter the amount of players you want to play with.");
                            }else {
                                result = REQUEST_GAME+ARGUMENT_SEPARATOR+size;
                            }
                        }else {
                            System.out.println("Please enter the amount of players you want to play with.");
                        }
                    }
                    case PLAY_CARD ->{
                        if (commandArray.length > 1){
                            String card = commandArray[1];
                            if (card.isEmpty()){
                                System.out.println("Please enter the card name you want to play.");
                            } else {
                                if(card.contains(",")){
                                    if(this.checkMultipleCardNames(this.formatCardName(card))) {
                                        result = PLAY_CARD+ARGUMENT_SEPARATOR+this.formatCardName(card);
                                    } else {
                                        System.out.println("Please enter valid card names.");
                                    }
                                } else {
                                    if(this.checkCardName(this.formatCardName(card))) {
                                        result = PLAY_CARD+ARGUMENT_SEPARATOR+this.formatCardName(card);
                                    } else {
                                        System.out.println("Please enter a valid card name.");
                                    }
                                }
                            }
                        } else {
                            System.out.println("Please enter the card name you want to play.");
                        }
                    }
                    case DRAW_CARD ->{
                        result = DRAW_CARD;
                    }
                    case SEND_MESSAGE->{
                        if (commandArray.length > 1){
                            String text = commandArray[1];
                            if (text.isEmpty()){
                                System.out.println("Please enter the message you want to send.");
                            }else {
                                result = SEND_MESSAGE+ARGUMENT_SEPARATOR+text;
                            }
                        }else {
                            System.out.println("Please enter the message you want to send.");
                        }
                    }
                    case RESPOND_YESORNO->{
                        if (commandArray.length > 1){
                            String yesOrNo = commandArray[1];
                            if (yesOrNo.isEmpty()){
                                System.out.println("Please enter Yes or No");
                            }else {
                                result = RESPOND_YESORNO + ARGUMENT_SEPARATOR+yesOrNo;
                            }
                        }else {
                            System.out.println("Please enter Yes or No");
                        }
                    }
                    case RESPOND_INDEX->{
                        if (commandArray.length >1){
                            String index = commandArray[1];
                            if (index.isEmpty()){
                                System.out.println("Please enter the index where you want to put the card");
                            }else {
                                result = RESPOND_INDEX+ARGUMENT_SEPARATOR+index;
                            }
                        }else {
                            System.out.println("Please enter the index where you want to put the card");
                        }
                    }
                    case RESPOND_PLAYERNAME->{
                        if (commandArray.length>1){
                            String playerName = commandArray[1];
                            if (playerName.isEmpty()){
                                System.out.println("Please enter the player name.");
                            }else {
                                result = RESPOND_PLAYERNAME+ARGUMENT_SEPARATOR+playerName;
                            }
                        }else {
                            System.out.println("Please enter the player name.");
                        }
                    }
                    case RESPOND_CARDNAME->{
                        if (commandArray.length>1){
                            String cardName = commandArray[1];
                            if (cardName.isEmpty()){
                                System.out.println("Please enter the card name.");
                            }else {
                                result = RESPOND_CARDNAME+ARGUMENT_SEPARATOR+cardName;
                            }
                        }else {
                            System.out.println("Please enter the card name.");
                        }
                    }
                    case STOP_SHUFFLE->{
                        result = STOP_SHUFFLE;
                    }
                    case "COMMAND" -> System.out.println(COMMAND_LINE);
                    default -> System.out.println(unknownCommand);
                }
            }
            //if player input don't have ~ , use Customer Command Line
            else {
                String[] commandArray = playerInput.split("-");
                String command = commandArray[0];

                switch (command.toLowerCase()){
                    case "connect" ->{
                        if (commandArray.length>2){
                            String playerName = commandArray[1];
                            String flags = commandArray[2];
                            if (playerName.isEmpty()){
                                System.out.println("Please enter player name to start connect.");
                            }else {
                                result = CONNECT_TO_SERVER+ARGUMENT_SEPARATOR+playerName+ARGUMENT_SEPARATOR+flags;
                            }
                        } else if (commandArray.length > 1) {
                            String playerName = commandArray[1];
                            if (playerName.isEmpty()){
                                System.out.println("Please enter player name to start connect.");
                            }else {
                                result = CONNECT_TO_SERVER+ARGUMENT_SEPARATOR+playerName;
                            }
                        }else {
                            System.out.println("Please enter player name to start connect.");
                        }
                    }
                    case "add computer"->{
                        System.out.println(YELLOW_BOLD + "\uD83E\uDD16 Welcome, Computer Whiz! \uD83D\uDCBE Someone just plugged in some digital magic. Ready for the techy vibes?" + RESET);
                        result = ADD_COMPUTER_PLAYER;
                    }
                    case "remove computer"->{
                        System.out.println(YELLOW_BOLD + "\uD83D\uDEA8 Uh-oh, the computer is taking a coffee break! ☕ Adiós, Computer Buddy!" + RESET);
                        result = REMOVE_COMPUTER_PLAYER;
                    }
                    case "request game" ->{
                        if (commandArray.length > 1){
                            String size = commandArray[1];
                            if (size.isEmpty()){
                                System.out.println("Please enter the amount of players you want to play with.");
                            }else {
                                result = REQUEST_GAME+ARGUMENT_SEPARATOR+size;
                            }
                        }else {
                            System.out.println("Please enter the amount of players you want to play with.");
                        }
                    }
                    case "play" ->{
                        if (commandArray.length > 1){
                            String card = commandArray[1];
                            if (card.isEmpty()){
                                System.out.println("Please enter the card name you want to play.");
                            } else {
                                if(card.contains(",")){
                                    if(this.checkMultipleCardNames(this.formatCardName(card))) {
                                        result = PLAY_CARD+ARGUMENT_SEPARATOR+this.formatCardName(card);
                                    } else {
                                        System.out.println("Please enter valid card names.");
                                    }
                                } else {
                                    if(this.checkCardName(this.formatCardName(card))) {
                                        result = PLAY_CARD+ARGUMENT_SEPARATOR+this.formatCardName(card);
                                    } else {
                                        System.out.println("Please enter a valid card name.");
                                    }
                                }
                            }
                        } else {
                            System.out.println("Please enter the card name you want to play.");
                        }
                    }
                    case "draw" ->{
                        result = DRAW_CARD;
                    }
                    case "send"->{
                        if (commandArray.length > 1){
                            String text = commandArray[1];
                            if (text.isEmpty()){
                                System.out.println("Please enter the message you want to send.");
                            }else {
                                result = SEND_MESSAGE+ARGUMENT_SEPARATOR+text;
                            }
                        }else {
                            System.out.println("Please enter the message you want to send.");
                        }
                    }
                    case "respond nope"->{
                        if (commandArray.length > 1){
                            String yesOrNo = commandArray[1];
                            if (yesOrNo.isEmpty()){
                                System.out.println("Please enter Yes or No");
                            }else {
                                result = RESPOND_YESORNO + ARGUMENT_SEPARATOR+yesOrNo;
                            }
                        }else {
                            System.out.println("Please enter Yes or No");
                        }
                    }
                    case "respond index"->{
                        if (commandArray.length >1){
                            String index = commandArray[1];
                            if (index.isEmpty()){
                                System.out.println("Please enter the index where you want to put the card");
                            }else {
                                result = RESPOND_INDEX+ARGUMENT_SEPARATOR+index;
                            }
                        }else {
                            System.out.println("Please enter the index where you want to put the card");
                        }
                    }
                    case "respond name"->{
                        if (commandArray.length>1){
                            String playerName = commandArray[1];
                            if (playerName.isEmpty()){
                                System.out.println("Please enter the player name.");
                            }else {
                                result = RESPOND_PLAYERNAME+ARGUMENT_SEPARATOR+playerName;
                            }
                        }else {
                            System.out.println("Please enter the player name.");
                        }
                    }
                    case "respond card"->{
                        if (commandArray.length>1){
                            String cardName = commandArray[1];
                            if (cardName.isEmpty()){
                                System.out.println("Please enter the card name.");
                            }else {
                                if(this.checkCardName(this.formatCardName(cardName))) {
                                    result = RESPOND_CARDNAME+ARGUMENT_SEPARATOR+this.formatCardName(cardName);
                                } else {
                                    System.out.println("Please enter a valid card name.");
                                }
                            }
                        }else {
                            System.out.println("Please enter the card name.");
                        }
                    }
                    case "stop"->{
                        result = STOP_SHUFFLE;
                    }
                    case "command"-> System.out.println(COMMAND_LINE);
                    default -> System.out.println(unknownCommand);
                }
            }
            if(!result.isEmpty()) {
                NETWORK_PLAYER.sendMessageToServer(result);
            }
        }
    }

    /**
     * This method is used to read the messages received from the server and to display them to the console.
     * @requires messageFromServer != null
     */
    public void printMessageFromServer(String messageFromServer) {
        String[] commandArray = messageFromServer.split(ARGUMENT_SEPARATOR);
        String command = commandArray[0];
        String result = "";
        switch (command){
            case HELLO ->{
                String playerName = commandArray[1];
                String flags = "CHAT, LOBBY, SPECIAL COMBOS";
                result = String.format(YELLOW_BOLD + """
                        🎉 Welcome to the purr-fect adventure, %s! 🐱 
                        Prepare for a wild ride in the world of Exploding Kittens. 
                        Grab your cards and get ready to pounce into the game – it's 
                        going to be a claw-some experience! 🚀 
                        Let the games begin, and may your moves be as unpredictable 
                        as a cat on catnip! 😺💥
                        Flags we support: %s
                        """ + RESET, playerName, flags);
            }
            case PLAYER_LIST->{
                String playerList = commandArray[1];
                result = String.format(YELLOW_BOLD + """
                        🎉 Get ready to welcome these rockstars to the game: %s! 🚀👾
                        Let the gaming party begin! 🎮💫
                        """ + RESET, playerList);
            }
            case QUEUE ->{
                String size = commandArray[1];
                result = String.format(YELLOW_BOLD + """
                        👫👬 Queue update: %s awesome players in the lobby! Ready for the fun trio!😻
                        """ + RESET, size);
            }
            case NEW_GAME->{
                String players = commandArray[1];
                result = String.format(YELLOW_BOLD + """
                        🎮 A new game has started with: %s!
                        """ + RESET, players);
            }
            case CURRENT->{
                String currentPlayer = commandArray[1];
                result = String.format(GREEN_BOLD_BRIGHT + """
                        👉Current Player: %s
                        """ + RESET, currentPlayer);
            }
            case SHOW_HAND->{
                String hand = commandArray[1];
                result = String.format(GREEN_BOLD_BRIGHT + """
                        🃏 Your current hand: %s
                        """ + RESET, hand);
            }
            case GAME_OVER->{
                String winner = commandArray[1];
                result = String.format(GREEN_BOLD_BRIGHT + """
                        🏆 Game over! And the winner is... %s! 🎉🥇 Well played! 🚀🎮
                        """ + RESET, winner);
            }
            case ERROR->{
                String error = commandArray[1];
                result = String.format(YELLOW_BOLD + """
                        🤯 Oops! We've encountered a wild exception: %s!💻
                        """ + RESET, error);
            }
            case BROADCAST_MOVE->{
                String player = commandArray[1];
                String move = commandArray[2];
                if(move.equalsIgnoreCase("DRAW_CARD")) {
                    result = String.format(GREEN_BOLD_BRIGHT + """
                        📢 Attention! %s took a card from the draw pile! 🎲
                        """ + RESET, player);
                } else {
                    result = String.format(GREEN_BOLD_BRIGHT + """
                            📢 Attention! %s just played a %s! 🎲
                            """ + RESET, player, move);
                }
            }
            case PLAYER_OUT->{
                String player = commandArray[1];
                result = String.format(YELLOW_BOLD + "\uD83D\uDE22Player %s is out of this game.\uD83D\uDC4B" + RESET, player);
            }
            case ASK_FOR_YESORNO->{
                result = YELLOW_BOLD + "Hey there! Do you want to play a Nope card? \uD83D\uDE0F " + RESET;
            }
            case ASK_STOP_SHUFFLE->{
                result = YELLOW_BOLD + "\uD83D\uDD04 Hold the shuffle! The deck is taking a breather. It'll stay put until you type 'STOP'. Ready when you are!" + RESET;
            }
            case ASK_FOR_INDEX->{
                String index = commandArray[1];
                result = String.format(YELLOW_BOLD + """
                        \uD83C\uDFB2 Time to make a move! Type a number from 0 to %s, use %s to put the Exploding Kitten on top of the draw pile!
                        Type the index where you want to insert the Exploding Kitten card back into the draw pile.
                        """ + RESET, index, index);
            }
            case ASK_FOR_CARDNAME->{
                result = YELLOW_BOLD + "\uD83C\uDCCF Enter the name of the card." + RESET;
            }
            case SHOW_MESSAGE->{
                String name = commandArray[1];
                String text = commandArray[2];
                result = String.format(YELLOW_BOLD + "\uD83D\uDCE2[%s]: %s" + RESET, name, text);
            }
            case ASK_FOR_PLAYERNAME->{
                String playerNames = commandArray[1];
                result = String.format(YELLOW_BOLD + """
                         \uD83C\uDCCF Sneaky move time! Enter the name of the player you want to steal a card from.
                         The players are: %s.
                         """ + RESET, playerNames);
            }
            case SHOW_FIRST_3_CARDS->{
                String firstThreeCards = commandArray[1];
                result = String.format(YELLOW_BOLD + "\uD83C\uDCCF The first three cards are %s!" + RESET, firstThreeCards);

            }
            case EXPLODING_KITTEN->{
                String name = commandArray[1];
                result = String.format(YELLOW_BOLD + """
                        🔥 Watch out! %s just drew an \uD83D\uDCA3Exploding Kitten\uD83D\uDCA5! 😱 Hold on tight, it's about to get explosive! 💣
                        """ + RESET, name);
            }
        }
        System.out.println(result);
    }

    /**
     * Format the name of a card such that each word starts with an uppercase letter and all other letters are lowercase.
     * For example: RaiNboW RaLpHinG CaT -> Rainbow Ralphing Cat
     * @param card the name of the card
     * @requires card != null
     * @return the new format of the card name
     */
    public String formatCardName(String card) {
        String result = String.valueOf(card.charAt(0)).toUpperCase();
        for(int i=1; i<card.length(); i++) {
            String letter = String.valueOf(card.charAt(i - 1));
            if(letter.equals(" ") || letter.equals(",")) {
                result += String.valueOf(card.charAt(i)).toUpperCase();
            } else {
                result += String.valueOf(card.charAt(i)).toLowerCase();
            }
        }
        return result;
    }

    /**
     * Check if the player has entered a valid card name.
     * @param cardName the card name entered by the player
     * @requires cardName != null
     * @return true if the entered card name is valid, false otherwise
     */
    public boolean checkCardName(String cardName) {
        for(CardType cardType : CardType.values()) {
            if(cardType.name().replace("_", " ").equalsIgnoreCase(cardName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if the player has entered valid card names when he plays cards in combo.
     * @param cardNames the card names entered by the player
     * @requires cardNames != null
     * @return true if the entered card names are valid, false otherwise
     */
    public boolean checkMultipleCardNames(String cardNames) {
        String[] cardNamesArray = cardNames.split(ELEMENT_SEPARATOR);
        int countCorrectCardNames = 0;
        int countCardsSeparator = 0;

        for(String cardName : cardNamesArray) {
            for(CardType cardType : CardType.values()) {
                if(cardType.name().replace("_", " ").equalsIgnoreCase(cardName)) {
                    countCorrectCardNames += 1;
                    break;
                }
            }
        }

        for(int i=0; i<cardNames.length(); i++) {
            if(String.valueOf(cardNames.charAt(i)).equals(",")) {
                countCardsSeparator += 1;
            }
        }

        if(countCardsSeparator == 2 && countCorrectCardNames == 3) {
            return true;
        } else {
            return countCardsSeparator == 1 && countCorrectCardNames == 2;
        }
    }
}
