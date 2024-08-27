package local.model;

import java.util.Collections;
import java.util.Stack;

/**
 * Class to represent a deck of cards in the Exploding Kittens game.
 * @author Alexandru-Cristian Enescu and Oliver Li
 */
public class Deck {
    private Stack<Card> drawPile;
    private Stack<Card> discardPile;

    /**
     * Create a deck of cards which consists of a draw pile and a discard pile.
     * Initialize the draw pile by adding to it all cards of the game, except Exploding Kittens cards and Defuse cards.
     * Initialize the discard pile with an empty stack.
     */
    public Deck() {
        this.drawPile = this.generateCards();
        this.discardPile = new Stack<>();
    }

    /**
     * Get all cards from the draw pile.
     * @return the draw pile
     */
    public Stack<Card> getDrawPile() {
        return this.drawPile;
    }

    /**
     * Get all cards from the discard pile.
     * @return the discard pile
     */
    public Stack<Card> getDiscardPile() {
        return this.discardPile;
    }

    /**
     * Create cards for the Exploding Kittens game.
     * @return an array consisting of all cards of the game, except Exploding Kittens cards and Defuse cards.
     */
    public Stack<Card> generateCards() {
        Stack<Card> cards = new Stack<>();
        for(int i=0; i<5; i++) {
            cards.add(new Card(CardType.NOPE));
            cards.add(new Card(CardType.SEE_THE_FUTURE));
        }
        for(int i=0; i<4; i++) {
            cards.add(new Card(CardType.ATTACK));
            cards.add(new Card(CardType.FAVOR));
            cards.add(new Card(CardType.SHUFFLE));
            cards.add(new Card(CardType.SKIP));
            cards.add(new Card(CardType.TACO_CAT));
            cards.add(new Card(CardType.HAIRY_POTATO_CAT));
            cards.add(new Card(CardType.RAINBOW_RALPHING_CAT));
            cards.add(new Card(CardType.BEARD_CAT));
            cards.add(new Card(CardType.CATTERMELON));
        }
        return cards;
    }

    /**
     * Set up the draw pile for a new game.
     * @param numberOfPlayers the number of players for which the deck is set
     * @ensures the number of Exploding Kitten cards is smaller by 1 than the number of players
     * @ensures if there are 2, 3 or 4 players, add 2 defuse cards into the draw pile
     * @ensures if there are 5 players, add 1 defuse card into the draw pile
     * @ensures shuffle the draw pile
     */
    public void setUpDeck(int numberOfPlayers) {
        for(int i=0; i<numberOfPlayers-1; i++){
            this.getDrawPile().add(new Card(CardType.EXPLODING_KITTEN));
        }
        switch(numberOfPlayers){
            case 2, 3, 4:
                this.getDrawPile().add(new Card(CardType.DEFUSE));
                this.getDrawPile().add(new Card(CardType.DEFUSE));
                break;
            case 5:
                this.getDrawPile().add(new Card(CardType.DEFUSE));
                break;
        }
        this.shuffleDrawPile();
    }

    /**
     * Add a card in the discard pile.
     * @param card the card to be added
     * @requires card != null
     * @ensures discardPile.size() += 1
     */
    public void addCardToDiscardPile(Card card) {
        this.getDiscardPile().push(card);
    }

    /**
     * Take a card from the draw pile.
     * @return the first card from the draw pile
     */
    public Card takeCardFromDrawPile() {
        return this.getDrawPile().pop();
    }

    /**
     * Shuffle the cards from the draw pile.
     */
    public void shuffleDrawPile() {
        Collections.shuffle(this.getDrawPile());
    }
}
