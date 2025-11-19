package test;

import exceptions.E12;
import network.model.NetworkPlayer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Test class for the NetworkPlayer. The ServerForNetworkPlayerTest must be run before running this class.
 * The connection and communication between the NetworkPlayer and the server is tested.
 * @author Oliver Li and Alexandru-Cristian Enescu
 */
public class NetworkPlayerTest {
    private NetworkPlayer networkPlayer;

    @BeforeEach
    public void setUp() {
        networkPlayer = new NetworkPlayer();
    }

    /**
     * Test the method connectToServer().
     * If NetworkPlayer successfully connects to the server, its Socket, BufferedReader and BufferedWriter cannot be null.
     */
    @Test
    public void testConnectToServer() {
        networkPlayer.connectToServer();
        assertNotNull(networkPlayer.getSocket());
        assertNotNull(networkPlayer.getBufferedReader());
        assertNotNull(networkPlayer.getBufferedWriter());
    }

    /**
     * Test if the NetworkPlayer successfully sends messages using the method sendMessageToServer() and can receive messages.
     * The server sends "Test message 1." which must be read by the NetworkPlayer's BufferedReader.
     * If the server receives "Test message 2", it sends back a confirmation message which contains the String "Message 2 received."
     */
    @Test
    public void testClientServerCommunication() throws E12 {
        networkPlayer.connectToServer();
        try {
            String testMessageOneReceived = networkPlayer.getBufferedReader().readLine();
            assertEquals("Test message 1.", testMessageOneReceived);

            networkPlayer.sendMessageToServer("Test message 2.");
            String testMessageTwoAnswer = networkPlayer.getBufferedReader().readLine();
            assertEquals("Message 2 received.", testMessageTwoAnswer);
        } catch (IOException e) {
            throw new E12();
        }
    }
}
