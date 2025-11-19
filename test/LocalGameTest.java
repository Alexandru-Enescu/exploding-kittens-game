package test;

import local.model.*;
import local.view.GameView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static local.model.CardType.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the logic of the local Exploding Kittens game.
 * @author Alexandru-Cristian Enescu and Oliver Li
 */
public class LocalGameTest {
    private LocalGame localGame;
    private Player oliver;
    private Player alex;
    private Player player3;

    /**
     * Sets initial values for the instance variables.
     * All test methods should be preceded by a call to this method.
     */
    @BeforeEach
    public void setUp() {
        ArrayList<String> playersNames = new ArrayList<>();
        playersNames.add("Oliver");
        playersNames.add("Alex");
        playersNames.add("Player 3");

        localGame = new LocalGame(playersNames, new GameView(), null);
        oliver = localGame.getPlayers().get(0);
        alex = localGame.getPlayers().get(1);
        player3 = localGame.getPlayers().get(2);
    }

    /**
     * Test if all 3 players have been added to the game.
     */
    @Test
    public void testNumberOfPlayers(){
        assertEquals(3, localGame.getPlayers().size());
    }

    /**
     * Test the method changeTurnToNextPlayer(), it should update the currentPlayerIndex and currentPlayer variables.
     */
    @Test
    public void testChangeTurnToNextPlayer() {
        // at the start of the game, the current player is the first player from the list of players
        assertEquals(0, localGame.getCurrentPlayerIndex());
        assertEquals(oliver, localGame.getCurrentPlayer());

        localGame.changeTurnToNextPlayer();

        // the currentPlayerIndex and currentPlayer variables have been updated
        assertEquals(1, localGame.getCurrentPlayerIndex());
        assertEquals(alex, localGame.getCurrentPlayer());
    }

    /**
     * Test the method getCurrentPlayerIndex(), it should return the index of the current player.
     */
    @Test
    public void testGetCurrentPlayerIndex(){
        // at the start of the game, the current player is the first player from the list of players
        assertEquals(0, localGame.getCurrentPlayerIndex());

        localGame.changeTurnToNextPlayer();
        assertEquals(1,localGame.getCurrentPlayerIndex());

        localGame.changeTurnToNextPlayer();
        assertEquals(2,localGame.getCurrentPlayerIndex());

        localGame.changeTurnToNextPlayer();
        assertEquals(0,localGame.getCurrentPlayerIndex());
    }

    /**
     * Test the method getNextPlayerIndex(), it should return the index of the player whose turn is after the current player.
     */
    @Test
    public void testGetNextPlayerIndex() {
        assertEquals(0, localGame.getCurrentPlayerIndex());
        assertEquals(1, localGame.getNextPlayerIndex());

        localGame.changeTurnToNextPlayer();
        assertEquals(1, localGame.getCurrentPlayerIndex());
        assertEquals(2, localGame.getNextPlayerIndex());

        localGame.changeTurnToNextPlayer();
        assertEquals(2, localGame.getCurrentPlayerIndex());
        assertEquals(0, localGame.getNextPlayerIndex());

        localGame.changeTurnToNextPlayer();
        assertEquals(0, localGame.getCurrentPlayerIndex());
        assertEquals(1, localGame.getNextPlayerIndex());
    }

    @Test
    public void testPlayerHand(){
        //before deal card
        assertEquals(0,oliver.getPlayerHandList().size());
        assertEquals(0,alex.getPlayerHandList().size());
        assertEquals(0,player3.getPlayerHandList().size());

        //deal card for each player
        localGame.setUpGame();

        // each player has 8 cards in their hand
        assertEquals(8,oliver.getPlayerHandList().size());
        assertEquals(8,alex.getPlayerHandList().size());
        assertEquals(8,player3.getPlayerHandList().size());

        //oliver draw one card
        Card card = localGame.drawCard(oliver);
        assertEquals(9,oliver.getPlayerHandList().size());

        //oliver play one card
        localGame.discardCard(oliver,2);
        assertEquals(8,oliver.getPlayerHandList().size());

        System.out.println("Oliver's cards: "+oliver.getPlayerHandString());
        System.out.println("Alex's cards: "+alex.getPlayerHandString());
        System.out.println("Player3's cards: "+player3.getPlayerHandString());

    }

    @Test
    public void testGame(){
        Card attack = new Card(ATTACK);
        Card skip = new Card(SKIP);
        Card shuffle = new Card(SHUFFLE);
        Card seeTheFuture = new Card(SEE_THE_FUTURE);

        assertFalse(localGame.gameOver());

        //play attack card on alex, change the turn, also add one card to discard pile
        assertEquals(0,localGame.getDeck().getDiscardPile().size());
        localGame.playCard(attack);
        System.out.println();
        assertEquals(1,localGame.getDeck().getDiscardPile().size());
        assertEquals(1,localGame.getCurrentPlayerIndex());

        //play shuffle card
        List<Card> drawPileBefore = new ArrayList<>(localGame.getDeck().getDrawPile());
        localGame.playCard(shuffle);
        System.out.println();
        assertEquals(2,localGame.getDeck().getDiscardPile().size());
        List<Card> drawPileAfter = new ArrayList<>(localGame.getDeck().getDrawPile());
        assertNotEquals(drawPileBefore,drawPileAfter);

        //play see the future card
        localGame.playCard(seeTheFuture);
        System.out.println();
        assertEquals(3,localGame.getDeck().getDiscardPile().size());

        //play skip card, change to next player
        localGame.playCard(skip);
        System.out.println();
        assertEquals(4,localGame.getDeck().getDiscardPile().size());
        assertEquals(alex,localGame.getCurrentPlayer());

        //remove one player, Two player left, not game over
        localGame.getPlayers().remove(localGame.getCurrentPlayerIndex());
        assertFalse(localGame.gameOver());

        //remove one player, one player left, game over.
        localGame.getPlayers().remove(localGame.getCurrentPlayerIndex());
        assertTrue(localGame.gameOver());
    }
}
