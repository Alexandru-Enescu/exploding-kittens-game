package local.model;

import java.util.ArrayList;

/**
 * Abstract Game class used to implement the functionality of the Exploding Kittens game which is common to the LocalGame and NetworkGame.
 * @author Oliver Li and Alexandru-Cristian Enescu
 */
public abstract class Game {
    protected ArrayList<Player> players;
    protected int currentPlayerIndex;
    protected Player currentPlayer;
    protected int additionalTurnsToPlay;
    protected boolean attackOn;
    protected Deck deck;

    /**
     * Create a game which can be either local or on network.
     * @param playersNames names of the players
     * Initialize the list of players and set the current player as the first player in the list.
     * Set additionalTurnsToPlay and attackOn variables to an initial value.
     * Initialize the Deck of cards.
     */
    public Game(ArrayList<String> playersNames) {
        this.players = createPlayers(playersNames);
        this.currentPlayerIndex = 0;
        this.currentPlayer = players.get(currentPlayerIndex);
        this.additionalTurnsToPlay = 0;
        this.attackOn = false;
        this.deck = new Deck();
    }

    /**
     * Get the deck of cards.
     * @return the deck of cards
     */
    public Deck getDeck() {
        return this.deck;
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public int getCurrentPlayerIndex() {
        return currentPlayerIndex;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    /**
     * Create players for the Exploding Kittens game.
     * @param playersNames the names of the players which will be added to the game
     * @return a list containing the players
     */
    public ArrayList<Player> createPlayers(ArrayList<String> playersNames) {
        ArrayList<Player> players = new ArrayList<>();
        for(String playerName : playersNames) {
            players.add(new Player(playerName));
        }
        return players;
    }

    /**
     * Set up the Exploding Kittens game.
     * @ensures give 1 defuse card and 7 random cards to each player
     */
    public void setUpGame() {
        deck.shuffleDrawPile();
        for(Player player : players) {
            player.addCard(new Card(CardType.DEFUSE));
            for(int i=0; i<7; i++) {
                player.addCard(deck.takeCardFromDrawPile());
            }
        }
        deck.setUpDeck(players.size());
    }

    /**
     * Draw a card from the draw pile.
     * @param player the player who draws the card
     * @requires player != null
     * @return the card taken by the player
     */
    public Card drawCard(Player player) {
        Card card = deck.takeCardFromDrawPile();
        player.addCard(card);
        return card;
    }

    /**
     * The current player will discard a card.
     * Add the card played by the current player to the discard pile and remove it from his hand
     * @param card the card played by the current player
     * @requires card != null
     */
    public void discardCard(Card card) {
        currentPlayer.getPlayerHandList().remove(card);
        deck.addCardToDiscardPile(card);
    }

    /**
     * Any player will discard a card.
     * This method is used when a "Nope" card is played, which is not necessarily played by the current player.
     * @param player the player who plays the card
     * @param cardIndex the index of the played card
     * @requires player != null, card != null
     */
    public void discardCard(Player player, int cardIndex) {
        Card card = player.getPlayerHandList().remove(cardIndex);
        deck.addCardToDiscardPile(card);
    }

    /**
     * Change the current player with the next player from the list of players.
     * @ensures <code>currentPlayer</code> and <code>currentPlayerIndex</code> are updated such that the next player becomes the current player
     */
    public void changeTurnToNextPlayer() {
        currentPlayerIndex = this.getNextPlayerIndex();
        currentPlayer = players.get(currentPlayerIndex);
    }

    /**
     * Get the index of the player after <code>currentPlayer</code>.
     * @ensures if <code>currentPlayer</code> is the last player in the list of players, index 0 is returned
     * @return index of next player
     */
    public int getNextPlayerIndex() {
        if(currentPlayerIndex == players.size() - 1) {
            return 0;
        } else {
            return currentPlayerIndex + 1;
        }
    }

    /**
     * The "Skip" card is played.
     * If the current player was not attacked, the next player becomes <code>currentPlayer</code>.
     * If the current player was attacked, the number of turns he has to play decreases by 1.
     * @ensures if <code>attackOn</code> is false, the next player becomes the current player
     * @ensures if <code>attackOn</code> is true, the value of <code>additionalTurnsToPlay</code> decreases by 1
     */
    public void playSkipCard(){
        this.changeTurnToNextPlayer();
        this.checkAttackOn();
    }

    /**
     * The "Shuffle" card is played.
     */
    public abstract void playShuffleCard();

    /**
     * The "Attack" card is played.
     * If a player attacks for the first time, the next player has 2 turns to play.
     * If an attacked player attacks again, the next player has to play the number of remaining turns plus 2 additional turns
     * @ensures <code>additionalTurnsToPlay</code> is updated, its value is set to 1 if an attack just started, otherwise it is increased by 2
     */
    public void playAttackCard(){
        if(additionalTurnsToPlay == 0 && !attackOn) {
            additionalTurnsToPlay = 1;
            this.attackOn = true;
        } else {
            additionalTurnsToPlay += 2;
        }
        this.changeTurnToNextPlayer();
    }

    /**
     * This method is called after the method changeTurnToNextPlayer() and it is used to check if the current player has been attacked.
     * If this is the case, a check is made to see if the current player has remaining turns to play.
     * @ensures if additionalTurnsToPlay != 0, the current player will keep his turn and his number of turns will decrease by 1.
     */
    public void checkAttackOn() {
        // if current player was attacked
        if(attackOn) {

            // if current player has remaining turns to play
            if(additionalTurnsToPlay != 0) {
                this.keepTurnCurrentPlayer();
                additionalTurnsToPlay -= 1;

            // if current player does not have remaining turns to play
            } else {
                attackOn = false;
            }
        }
    }

    /**
     * The current player keeps his turn.
     * This method is called after method <code>changeTurnToNextPlayer()</code> in order to cancel its effect.
     * @ensures <code>currentPlayer</code> and <code>currentPlayerIndex</code> are updated such that the previous player becomes the current player
     */
    public void keepTurnCurrentPlayer() {
        if(currentPlayerIndex == 0) {
            currentPlayerIndex = players.size() - 1;
        } else {
            currentPlayerIndex -= 1;
        }
        currentPlayer = players.get(currentPlayerIndex);
    }

    /**
     * Check if the game is over.
     * @return true if there is only 1 player left in the game, false otherwise
     * @ensures returns true if players.size() == 1
     */
    public boolean gameOver() {
        return this.players.size() == 1;
    }
}
