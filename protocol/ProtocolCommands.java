package protocol;

public class ProtocolCommands {

    /**
     * The messages that are going to be used in this protocol are Strings, and the arguments are separated with the
     * symbol “ ~ ”. If an argument consists of more elements, then they are split by the symbol comma " , ".
     */
    public static final String ARGUMENT_SEPARATOR = "~";
    public static final String ELEMENT_SEPARATOR = ",";

    public enum Flags {
        CHAT, TEAMS, MULTI_GAMES, LOBBY, COMBOS, EXTENSION, GUI
    }

    /**
     * Commands used when a Client sends a message to the Server.
     */
    public static final String CONNECT_TO_SERVER = "CONNECT";
    public static final String ADD_COMPUTER_PLAYER = "ADD_COMPUTER";
    public static final String REMOVE_COMPUTER_PLAYER = "REMOVE_COMPUTER";
    public static final String REQUEST_GAME = "REQUEST_GAME";
    public static final String PLAY_CARD = "PLAY_CARD";
    public static final String DRAW_CARD = "DRAW_CARD";
    public static final String SEND_MESSAGE = "SEND";
    public static final String RESPOND_PLAYERNAME = "RESPOND_PLAYERNAME";
    public static final String RESPOND_CARDNAME = "RESPOND_CARDNAME";
    public static final String RESPOND_INDEX = "RESPOND_INDEX";
    public static final String RESPOND_YESORNO = "RESPOND_YESORNO";
    public static final String STOP_SHUFFLE = "STOP_SHUFFLE";

    /**
     * Commands used when the Server sends a message to a Client.
     */
    public static final String HELLO = "HELLO";
    public static final String PLAYER_LIST = "PLAYER_LIST";
    public static final String QUEUE = "QUEUE";
    public static final String NEW_GAME = "NEW_GAME";
    public static final String CURRENT = "CURRENT";
    public static final String SHOW_HAND = "SHOW_HAND";
    public static final String GAME_OVER = "GAME_OVER";
    public static final String ERROR = "ERROR";
    public static final String BROADCAST_MOVE = "BROADCAST_MOVE";
    public static final String PLAYER_OUT = "PLAYER_OUT";
    public static final String SHOW_MESSAGE = "MESSAGE";
    public static final String ASK_FOR_PLAYERNAME = "ASK_FOR_PLAYERNAME"; // (if Favor card is played / cards were played in combo)
    public static final String ASK_FOR_INDEX = "ASK_FOR_INDEX"; // (if Defuse card is played)
    public static final String ASK_FOR_YESORNO = "ASK_FOR_YESORNO"; // (ask to play the Nope card)
    public static final String ASK_STOP_SHUFFLE = "ASK_STOP_SHUFFLE"; // (if Shuffle card is played)
    public static final String ASK_FOR_CARDNAME = "ASK_FOR_CARDNAME"; // (if Favor card is played / 3 cards were played in combo)
    public static final String SHOW_FIRST_3_CARDS = "SHOW_FIRST_3_CARDS"; // (if See The Future card is played)
    public static final String EXPLODING_KITTEN = "EXPLODING_KITTEN"; // (if Exploding Kitten is taken by a player)
}
