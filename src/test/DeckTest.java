package test;

import local.model.Card;
import local.model.CardType;
import local.model.Deck;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the deck of Exploding Kittens game.
 * @author Oliver Li and Alexandru-Cristian Enescu
 */
public class DeckTest {
    private Deck deck;

    /**
     * Sets an initial value for the instance variable <tt>deck</tt>.
     * All test methods should be preceded by a call to this method.
     */
    @BeforeEach
    public void setUp() {
        this.deck = new Deck();
    }

    /**
     * The cards of the deck should be differently ordered.
     */
    @Test
    public void testShuffleDrawPile() {
        String firstCardBeforeShuffle = deck.getDrawPile().peek().toString();
        deck.shuffleDrawPile();
        String firstCardAfterShuffle = deck.getDrawPile().peek().toString();

        assertFalse(firstCardBeforeShuffle.contains(firstCardAfterShuffle));
    }

    /**
     * For a game with 2 players, in the deck should be left 2 Defuse cards and 1 Exploding Kitten card.
     */
    @Test
    public void testSetUpDeckForTwoPlayers() {
        deck.setUpDeck(2);

        ArrayList<Card> defuseCards = new ArrayList<>();
        ArrayList<Card> explodingKittensCards = new ArrayList<>();

        for(Card card : deck.getDrawPile()) {
            if(card.getCardType().equals(CardType.DEFUSE)) {
                defuseCards.add(card);
            }
            if(card.getCardType().equals(CardType.EXPLODING_KITTEN)) {
                explodingKittensCards.add(card);
            }
        }
        assertEquals(2, defuseCards.size());
        assertEquals(1, explodingKittensCards.size());
    }

    /**
     * For a game with 3 players, in the deck should be left 2 Defuse cards and 2 Exploding Kitten cards.
     */
    @Test
    public void testSetUpDeckForThreePlayers() {
        deck.setUpDeck(3);

        ArrayList<Card> defuseCards = new ArrayList<>();
        ArrayList<Card> explodingKittensCards = new ArrayList<>();

        for(Card card : deck.getDrawPile()) {
            if(card.getCardType().equals(CardType.DEFUSE)) {
                defuseCards.add(card);
            }
            if(card.getCardType().equals(CardType.EXPLODING_KITTEN)) {
                explodingKittensCards.add(card);
            }
        }
        assertEquals(2, defuseCards.size());
        assertEquals(2, explodingKittensCards.size());
    }

    /**
     * For a game with 4 players, in the deck should be left 2 Defuse cards and 3 Exploding Kitten cards.
     */
    @Test
    public void testSetUpDeckForFourPlayers() {
        deck.setUpDeck(4);
        ArrayList<Card> defuseCards = new ArrayList<>();
        ArrayList<Card> explodingKittensCards = new ArrayList<>();

        for(Card card : deck.getDrawPile()) {
            if(card.getCardType().equals(CardType.DEFUSE)) {
                defuseCards.add(card);
            }
            if(card.getCardType().equals(CardType.EXPLODING_KITTEN)) {
                explodingKittensCards.add(card);
            }
        }
        assertEquals(2, defuseCards.size());
        assertEquals(3, explodingKittensCards.size());
    }

    /**
     * For a game with 5 players, in the deck should be 1 Defuse card and 4 Exploding Kitten cards.
     */
    @Test
    public void testSetUpDeckForFivePlayers() {
        deck.setUpDeck(5);
        ArrayList<Card> defuseCards = new ArrayList<>();
        ArrayList<Card> explodingKittensCards = new ArrayList<>();

        for(Card card : deck.getDrawPile()) {
            if(card.getCardType().equals(CardType.DEFUSE)) {
                defuseCards.add(card);
            }
            if(card.getCardType().equals(CardType.EXPLODING_KITTEN)) {
                explodingKittensCards.add(card);
            }
        }
        assertEquals(1, defuseCards.size());
        assertEquals(4, explodingKittensCards.size());
    }
}
