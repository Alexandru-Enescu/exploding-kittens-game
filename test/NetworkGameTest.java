package test;

import exceptions.E13;
import local.model.Card;
import local.model.CardType;
import local.model.Player;
import network.model.NetworkGame;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the NetworkGame.
 * @author Oliver Li and Alexandru-Cristian Enescu
 */
public class NetworkGameTest {
    private NetworkGame networkGame;

    /**
     * Initialize a new list of players and create a new NetworkGame.
     */
    @BeforeEach
    public void setUp() {
        ArrayList<String> playersNames = new ArrayList<>();
        playersNames.add("Player 1");
        playersNames.add("Player 2");
        networkGame = new NetworkGame(playersNames);
        networkGame.setUpGame();
    }

    /**
     * Test the method playSeeTheFutureCard().
     * This method must return a String which contains the first 3 cards from the top of the draw pile.
     */
    @Test
    public void testPlaySeeTheFutureCard() {
        String firstCard = networkGame.getDeck().getDrawPile().peek().toString();
        String secondCard = networkGame.getDeck().getDrawPile().get(networkGame.getDeck().getDrawPile().size() - 2).toString();
        String thirdCard = networkGame.getDeck().getDrawPile().get(networkGame.getDeck().getDrawPile().size() - 3).toString();
        String expectedResult = firstCard + "," + secondCard + "," + thirdCard;

        String actualResult = networkGame.playSeeTheFutureCard();
        assertEquals(expectedResult, actualResult);
    }

    /**
     * Test the method playFavorCard().
     * This method receives as arguments the player who must give a card to the current player and the card he wants to give.
     * Each player starts the game with 1 Defuse card. After this method is called, the current player must have 2 Defuse
     * cards in his hand and the other player should not have any, since he has given his Defuse card to the current player.
     */
    @Test
    public void testPlayFavorCard() {
        Player currentPlayer = networkGame.getCurrentPlayer();
        Player playerWhoGivesCard = networkGame.getPlayers().get(1);
        networkGame.playFavorCard(playerWhoGivesCard, "Defuse");

        int defuseCardsAtCurrentPlayer = 0;
        for(Card card : currentPlayer.getPlayerHandList()) {
            if(card.toString().contains("Defuse")) {
                defuseCardsAtCurrentPlayer += 1;
            }
        }
        assertEquals(2, defuseCardsAtCurrentPlayer);
        assertFalse(playerWhoGivesCard.getPlayerHandString().contains("Defuse"));
    }

    /**
     * Test the method playSpecialComboTwoCards().
     * This method must take a random card from the player given as an argument and give the card to the current player.
     * After this method is called, the current player must have 1 more card in his hand, and the player whose card was stolen
     * must have 1 card less in his hand.
     */
    @Test
    public void testPlaySpecialComboTwoCards() {
        Player currentPlayer = networkGame.getCurrentPlayer();
        Player nextPlayer = networkGame.getPlayers().get(1);

        int numberCardsCurrentPlayerBeforePlaying = currentPlayer.getPlayerHandList().size();
        int numberCardsNextPlayerBeforePlaying = nextPlayer.getPlayerHandList().size();
        networkGame.playSpecialComboTwoCards(nextPlayer);
        int numberCardsCurrentPlayerAfterPlaying = currentPlayer.getPlayerHandList().size();
        int numberCardsNextPlayerAfterPlaying = nextPlayer.getPlayerHandList().size();

        // current player: the number of cards after playing must be greater by 1 than the number of cards before playing
        assertEquals(numberCardsCurrentPlayerAfterPlaying, numberCardsCurrentPlayerBeforePlaying + 1);

        // other player: the number of cards after playing must be smaller by 1 than the number of cards before playing
        assertEquals(numberCardsNextPlayerAfterPlaying, numberCardsNextPlayerBeforePlaying - 1);
    }

    /**
     * Test the method playSpecialComboThreeCards().
     * This method receives as arguments the player whose card will be stolen by the current player and the card to be stolen.
     * Each player starts the game with 1 Defuse card. After this method is called, the current player must have 2 Defuse
     * cards in his hand and the other player should not have any, since the current player has stolen his Defuse card.
     */
    @Test
    public void testPlaySpecialComboThreeCards() {
        Player currentPlayer = networkGame.getCurrentPlayer();
        Player playerToStealCardFrom = networkGame.getPlayers().get(1);
        networkGame.playSpecialComboThreeCards(playerToStealCardFrom, "Defuse");

        int defuseCardsAtCurrentPlayer = 0;
        for(Card card : currentPlayer.getPlayerHandList()) {
            if(card.toString().contains("Defuse")) {
                defuseCardsAtCurrentPlayer += 1;
            }
        }
        assertEquals(2, defuseCardsAtCurrentPlayer);
        assertFalse(playerToStealCardFrom.getPlayerHandString().contains("Defuse"));
    }

    /**
     * Test the method checkCombo(). This method receives as an argument a list with card names which were played in combo,
     * and it must check if all the cards have the same type.
     * If the game is not played with special combos, the method should not allow action cards to be played in combo.
     */
    @Test
    public void testCheckCombo() throws E13 {
        // the method should throw an exception, the cards type is not the same
        ArrayList<String> cardsList1 = new ArrayList<>();
        cardsList1.add("Hairy Potato Cat");
        cardsList1.add("Beard Cat");
        cardsList1.add("Hairy Potato Cat");

        assertThrows(E13.class, () -> {
            networkGame.checkCombo(cardsList1, false);
        });

        // the method should not throw any exception, the cards have the same type
        ArrayList<String> cardsList2 = new ArrayList<>();
        cardsList2.add("Beard Cat");
        cardsList2.add("Beard Cat");
        cardsList2.add("Beard Cat");

        assertDoesNotThrow(() -> {
            networkGame.checkCombo(cardsList2, false);
        });

        // the method should throw an exception, the cards played in combo are action cards and special combos are not active
        ArrayList<String> cardsList3 = new ArrayList<>();
        cardsList3.add("Nope");
        cardsList3.add("Nope");
        cardsList3.add("Nope");

        assertThrows(E13.class, () -> {
            networkGame.checkCombo(cardsList3, false);
        });

        // the method should not throw any exception, the cards played are action cards and special combos are active
        ArrayList<String> cardsList4 = new ArrayList<>();
        cardsList4.add("Nope");
        cardsList4.add("Nope");
        cardsList4.add("Nope");

        assertDoesNotThrow(() -> {
            networkGame.checkCombo(cardsList4, true);
        });
    }

    /**
     * Test the method checkForDefuseCard(). This method must return true if the current player has a Defuse card in his hand.
     */
    @Test
    public void testCheckForDefuseCard() {
        Player currentPlayer = networkGame.getCurrentPlayer();

        // when the game starts, one Defuse card is given to each player, the method should return true
        assertTrue(networkGame.checkForDefuseCard());

        // when all Defuse cards are removed from the current player's hand, the method should return false
        for(int i=0; i<currentPlayer.getPlayerHandList().size(); i++) {
            String card = currentPlayer.getPlayerHandList().get(i).toString();
            if(card.contains("Defuse")) {
                currentPlayer.getPlayerHandList().remove(i);
                i--;
            }
        }
        assertFalse(networkGame.checkForDefuseCard());
    }

    /**
     * Test the method playExplodingKittenCard(). This method must remove the current player from the game, and it must update
     * the currentPlayer variable with the next player.
     */
    @Test
    public void testPlayExplodingKittenCard() {
        networkGame.playExplodingKittenCard();

        // there should be only 1 player left in the game
        assertEquals(1, networkGame.getPlayers().size());

        // the currentPlayer variable should be updated
        assertEquals("Player 2", networkGame.getCurrentPlayer().getName());
    }

    /**
     * Test the method playDefuseCard(). This method must insert an Exploding Kitten card in the draw pile at the index it
     * receives as argument. A Defuse card and the Exploding Kitten card must be discarded from the current player's hand.
     */
    @Test
    public void testPlayDefuseCard() {
        // add an Exploding Kitten card in the current player's hand
        Player currentPlayer = networkGame.getCurrentPlayer();
        currentPlayer.addCard(new Card(CardType.EXPLODING_KITTEN));

        networkGame.playDefuseCard("15");

        // check if the card at index 15 is an Exploding Kitten
        String cardIndex15 = networkGame.getDeck().getDrawPile().get(15).toString();
        assertTrue(cardIndex15.contains("Exploding Kitten"));

        // check the current player does not have the Exploding Kitten in his hand anymore
        assertFalse(currentPlayer.getPlayerHandString().contains("Exploding Kitten"));

        // check the current player does not have a Defuse card in his hand (he had only 1, since the game just started)
        assertFalse(currentPlayer.getPlayerHandString().contains("Defuse"));
    }

    /**
     * Test the method hasCard(). This method should return true if a player has in his hand the card given as argument.
     */
    @Test
    public void testHasCard() {
        Player currentPlayer = networkGame.getCurrentPlayer();

        // at the start of the game, nobody can have an Exploding Kitten card in his hand
        assertFalse(networkGame.hasCard(currentPlayer.getName(), "Exploding Kitten"));

        // give an Exploding Kitten card to the current player and check if the method returns true
        currentPlayer.addCard(new Card(CardType.EXPLODING_KITTEN));
        assertTrue(networkGame.hasCard(currentPlayer.getName(), "Exploding Kitten"));
    }

    /**
     * Test the method hasCards(). This method should return true if a player has in his hand multiple cards, these are given as argument.
     */
    @Test
    public void testHasCards() {
        Player nextPlayer = networkGame.getPlayers().get(1);

        // at the start of the game, nobody can have an Exploding Kitten card in his hand
        assertFalse(networkGame.hasCards(nextPlayer.getName(), "Exploding Kitten"));

        // give three Exploding Kitten cards to the next player, the method should return true
        nextPlayer.addCard(new Card(CardType.EXPLODING_KITTEN));
        nextPlayer.addCard(new Card(CardType.EXPLODING_KITTEN));
        nextPlayer.addCard(new Card(CardType.EXPLODING_KITTEN));
        assertTrue(networkGame.hasCards(nextPlayer.getName(), "Exploding Kitten,Exploding Kitten,Exploding Kitten"));
    }

    /**
     * Test the method noNopeCardsAtPlayers(). This method should return true only if there is no player who has a Nope card in his hand.
     */
    @Test
    public void testNoNopeCardsAtPlayers() {
        // remove Nope cards from all players hands, the method should return true
        for(Player player : networkGame.getPlayers()) {
            player.getPlayerHandList().removeIf(card -> card.toString().contains("Nope"));
        }
        assertTrue(networkGame.noNopeCardsAtPlayers());

        // give a Nope card to the current player, the method should return false
        networkGame.getCurrentPlayer().addCard(new Card(CardType.NOPE));
        assertFalse(networkGame.noNopeCardsAtPlayers());
    }

    /**
     * Test the method discardCard(). The card which is discarded should not be in the player's hand anymore, and it should
     * be added to the discard pile.
     */
    @Test
    public void testDiscardCard() {
        Player currentPlayer = networkGame.getCurrentPlayer();

        // when the game starts, each player has a Defuse card, after the method is called, the current player should not have one
        networkGame.discardCard("Defuse", currentPlayer.getName());
        assertFalse(networkGame.getCurrentPlayer().getPlayerHandString().contains("Defuse"));

        // check if the discard pile contains only 1 card which is the Defuse card
        boolean hasDefuse = false;
        for(Card card : networkGame.getDeck().getDiscardPile()) {
            if(card.toString().contains("Defuse")) {
                hasDefuse = true;
            }
        }
        assertEquals(1, networkGame.getDeck().getDiscardPile().size());
        assertTrue(hasDefuse);
    }

    /**
     * Test the method discardCards(). The cards which are discarded should not be in the player's hand anymore, and they should
     * be added to the discard pile.
     */
    @Test
    public void testDiscardCards() {
        Player currentPlayer = networkGame.getCurrentPlayer();

        // give 3 Exploding Kitten cards to the current player which are going to be discarded
        currentPlayer.addCard(new Card(CardType.EXPLODING_KITTEN));
        currentPlayer.addCard(new Card(CardType.EXPLODING_KITTEN));
        currentPlayer.addCard(new Card(CardType.EXPLODING_KITTEN));

        networkGame.discardCards("Exploding Kitten", 3);
        assertFalse(networkGame.getCurrentPlayer().getPlayerHandString().contains("Exploding Kitten"));

        // check if the discard pile contains 3 cards which are the Exploding Kitten cards
        int count = 0;
        for(Card card : networkGame.getDeck().getDiscardPile()) {
            if(card.toString().contains("Exploding Kitten")) {
                count += 1;
            }
        }
        assertEquals(3, networkGame.getDeck().getDiscardPile().size());
        assertEquals(3, count);
    }

    /**
     * Test the method playSkipCard(). The currentPlayer variable must be updated.
     */
    @Test
    public void testPlaySkipCard() {
        Player currentPlayerBefore = networkGame.getCurrentPlayer();
        networkGame.playSkipCard();
        Player currentPlayerAfter = networkGame.getCurrentPlayer();
        assertNotEquals(currentPlayerBefore, currentPlayerAfter);
    }

    /**
     * Test the method getPlayersNames(), it should return the name of the players separated by ","
     */
    @Test
    public void testGetPlayersNames() {
        assertEquals("Player 1,Player 2", networkGame.getPlayersNames());
    }
}
