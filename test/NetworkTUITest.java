package test;

import network.model.NetworkPlayer;
import network.view.NetworkTUI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import protocol.ProtocolCommands;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the NetworkTUI.
 * @author Alexandru-Cristian Enescu and Oliver Li
 */
public class NetworkTUITest {
    private NetworkTUI networkTUI;

    @BeforeEach
    public void setUp() {
        networkTUI = new NetworkTUI(new NetworkPlayer());
    }

    /**
     * Test the method formatCardName. The method must format the name of a card such that each word starts with an uppercase letter
     * and all other letters are lowercase. It must work also for cards with a name which contains more words.
     */
    @Test
    public void testFormatCardName() {
        String cardName1 = networkTUI.formatCardName("RaiNboW raLpHinG cAt");
        assertEquals("Rainbow Ralphing Cat", cardName1);

        String cardName2 = networkTUI.formatCardName("ShuFfLe,sHufFlE");
        assertEquals("Shuffle,Shuffle", cardName2);

        String cardName3 = networkTUI.formatCardName("haiRy pOtaTo CaT,HaiRY PoTaTo cAt,hAiRy PoTAto caT");
        assertEquals("Hairy Potato Cat,Hairy Potato Cat,Hairy Potato Cat", cardName3);
    }

    /**
     * Test the method checkCardName(). It must return true only if a valid card name is entered.
     */
    @Test
    public void testCheckCardName() {
        boolean wrongCardName1 = networkTUI.checkCardName("shuffl");
        assertFalse(wrongCardName1);

        boolean correctCardName1 = networkTUI.checkCardName("shuffle");
        assertTrue(correctCardName1);

        boolean wrongCardName2 = networkTUI.checkCardName("hairy potato");
        assertFalse(wrongCardName2);

        boolean correctCardName2 = networkTUI.checkCardName("hairy potato cat");
        assertTrue(correctCardName2);
    }

    /**
     * Test the method checkMultipleCardNames(). This method is used to check if multiple card names separated by "," are valid.
     */
    @Test
    public void testCheckMultipleCardNames() {
        boolean wrongCardNames1 = networkTUI.checkMultipleCardNames("favor,favo,favor");
        assertFalse(wrongCardNames1);

        boolean correctCardNames1 = networkTUI.checkMultipleCardNames("favor,favor,favor");
        assertTrue(correctCardNames1);

        boolean wrongCardNames2 = networkTUI.checkMultipleCardNames("taco cat,taco cat,tco cat");
        assertFalse(wrongCardNames2);

        boolean correctCardNames2 = networkTUI.checkMultipleCardNames("taco cat,taco cat,taco cat");
        assertTrue(correctCardNames2);
    }

    /**
     * Test the method printMessageFromServer(). The method receives messages in which the protocol commands are used,
     * it must be able to read the messages received and print to the console the corresponding messages used for the TUI.
     */
    @Test
    public void testPrintMessageFromServer() {
        System.out.println("Message to test:\n" + ProtocolCommands.HELLO + ProtocolCommands.ARGUMENT_SEPARATOR + "Player 1");
        System.out.println("\nMessage printed:");
        networkTUI.printMessageFromServer(ProtocolCommands.HELLO + ProtocolCommands.ARGUMENT_SEPARATOR + "Player 1");

        System.out.println("Message to test:\n" + ProtocolCommands.PLAYER_LIST + ProtocolCommands.ARGUMENT_SEPARATOR + "Player 1,Player 2");
        System.out.println("\nMessage printed:");
        networkTUI.printMessageFromServer(ProtocolCommands.PLAYER_LIST + ProtocolCommands.ARGUMENT_SEPARATOR + "Player 1,Player 2");

        System.out.println("Message to test:\n" + ProtocolCommands.QUEUE + ProtocolCommands.ARGUMENT_SEPARATOR + "2");
        System.out.println("\nMessage printed:");
        networkTUI.printMessageFromServer(ProtocolCommands.QUEUE + ProtocolCommands.ARGUMENT_SEPARATOR + "2");

        System.out.println("Message to test:\n" + ProtocolCommands.NEW_GAME + ProtocolCommands.ARGUMENT_SEPARATOR + "Player 1,Player 2");
        System.out.println("\nMessage printed:");
        networkTUI.printMessageFromServer(ProtocolCommands.NEW_GAME + ProtocolCommands.ARGUMENT_SEPARATOR + "Player 1,Player 2");

        System.out.println("Message to test:\n" + ProtocolCommands.CURRENT + ProtocolCommands.ARGUMENT_SEPARATOR + "Player 1");
        System.out.println("\nMessage printed:");
        networkTUI.printMessageFromServer(ProtocolCommands.CURRENT + ProtocolCommands.ARGUMENT_SEPARATOR + "Player 1");

        System.out.println("Message to test:\n" + ProtocolCommands.SHOW_HAND + ProtocolCommands.ARGUMENT_SEPARATOR + "Defuse,Favor,Skip,Shuffle,Rainbow Ralphing Cat");
        System.out.println("\nMessage printed:");
        networkTUI.printMessageFromServer(ProtocolCommands.SHOW_HAND + ProtocolCommands.ARGUMENT_SEPARATOR + "Defuse,Favor,Skip,Shuffle,Rainbow Ralphing Cat");

        System.out.println("Message to test:\n" + ProtocolCommands.GAME_OVER + ProtocolCommands.ARGUMENT_SEPARATOR + "Player 2");
        System.out.println("\nMessage printed:");
        networkTUI.printMessageFromServer(ProtocolCommands.GAME_OVER + ProtocolCommands.ARGUMENT_SEPARATOR + "Player 2");

        System.out.println("Message to test:\n" + ProtocolCommands.ERROR + ProtocolCommands.ARGUMENT_SEPARATOR + "Name already used");
        System.out.println("\nMessage printed:");
        networkTUI.printMessageFromServer(ProtocolCommands.ERROR + ProtocolCommands.ARGUMENT_SEPARATOR + "Name already used");

        System.out.println("Message to test:\n" + ProtocolCommands.BROADCAST_MOVE + ProtocolCommands.ARGUMENT_SEPARATOR + "Player 1" + ProtocolCommands.ARGUMENT_SEPARATOR + "Shuffle");
        System.out.println("\nMessage printed:");
        networkTUI.printMessageFromServer(ProtocolCommands.BROADCAST_MOVE + ProtocolCommands.ARGUMENT_SEPARATOR + "Player 1" + ProtocolCommands.ARGUMENT_SEPARATOR + "Shuffle");

        System.out.println("Message to test:\n" + ProtocolCommands.PLAYER_OUT + ProtocolCommands.ARGUMENT_SEPARATOR + "Player 1");
        System.out.println("\nMessage printed:");
        networkTUI.printMessageFromServer(ProtocolCommands.PLAYER_OUT + ProtocolCommands.ARGUMENT_SEPARATOR + "Player 1");

        System.out.println("\nMessage to test:\n" + ProtocolCommands.ASK_FOR_YESORNO);
        System.out.println("\nMessage printed:");
        networkTUI.printMessageFromServer(ProtocolCommands.ASK_FOR_YESORNO);

        System.out.println("\nMessage to test:\n" + ProtocolCommands.ASK_STOP_SHUFFLE);
        System.out.println("\nMessage printed:");
        networkTUI.printMessageFromServer(ProtocolCommands.ASK_STOP_SHUFFLE);

        System.out.println("\nMessage to test:\n" + ProtocolCommands.ASK_FOR_INDEX + ProtocolCommands.ARGUMENT_SEPARATOR + "37");
        System.out.println("\nMessage printed:");
        networkTUI.printMessageFromServer(ProtocolCommands.ASK_FOR_INDEX + ProtocolCommands.ARGUMENT_SEPARATOR + "37");

        System.out.println("\nMessage to test:\n" + ProtocolCommands.ASK_FOR_CARDNAME);
        System.out.println("\nMessage printed:");
        networkTUI.printMessageFromServer(ProtocolCommands.ASK_FOR_CARDNAME);

        System.out.println("\nMessage to test:\n" + ProtocolCommands.SHOW_MESSAGE + ProtocolCommands.ARGUMENT_SEPARATOR + "Player 1" + ProtocolCommands.ARGUMENT_SEPARATOR + "hello!");
        System.out.println("\nMessage printed:");
        networkTUI.printMessageFromServer(ProtocolCommands.SHOW_MESSAGE + ProtocolCommands.ARGUMENT_SEPARATOR + "Player 1" + ProtocolCommands.ARGUMENT_SEPARATOR + "hello!");

        System.out.println("\nMessage to test:\n" + ProtocolCommands.ASK_FOR_PLAYERNAME + ProtocolCommands.ARGUMENT_SEPARATOR + "Player 1,Player 2");
        System.out.println("\nMessage printed:");
        networkTUI.printMessageFromServer(ProtocolCommands.ASK_FOR_PLAYERNAME + ProtocolCommands.ARGUMENT_SEPARATOR + "Player 1,Player 2");

        System.out.println("\nMessage to test:\n" + ProtocolCommands.SHOW_FIRST_3_CARDS + ProtocolCommands.ARGUMENT_SEPARATOR + "Favor,Defuse,Exploding Kitten");
        System.out.println("\nMessage printed:");
        networkTUI.printMessageFromServer(ProtocolCommands.SHOW_FIRST_3_CARDS + ProtocolCommands.ARGUMENT_SEPARATOR + "Favor,Defuse,Exploding Kitten");

        System.out.println("\nMessage to test:\n" + ProtocolCommands.EXPLODING_KITTEN + ProtocolCommands.ARGUMENT_SEPARATOR + "Player 1");
        System.out.println("\nMessage printed:");
        networkTUI.printMessageFromServer(ProtocolCommands.EXPLODING_KITTEN + ProtocolCommands.ARGUMENT_SEPARATOR + "Player 1");

    }
}
