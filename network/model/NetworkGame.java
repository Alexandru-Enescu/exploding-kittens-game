package network.model;

import exceptions.E13;
import local.model.Card;
import local.model.Game;
import local.model.Player;
import java.util.ArrayList;

/**
 * Class which contains the functionality necessary for playing a network Exploding Kittens game.
 * @author Oliver Li and Alexandru-Cristian Enescu
 */
public class NetworkGame extends Game {
    private boolean keepShuffle;

    /**
     * Create a NetworkGame and initialize the list of players.
     * @param namesOfHumanPlayers the names of the players
     */
    public NetworkGame(ArrayList<String> namesOfHumanPlayers) {
        super(namesOfHumanPlayers);
        this.keepShuffle = false;
    }

    /**
     * Change the value of the keepShuffle variable.
     */
    public synchronized void setKeepShuffle(boolean keepShuffle) {
        this.keepShuffle = keepShuffle;
    }

    /**
     * Get the value of the keepShuffle variable.
     */
    public synchronized boolean isKeepShuffle() {
        return this.keepShuffle;
    }

    /**
     * Get the players of the game.
     * @return a list which contains the players of the game
     */
    public synchronized ArrayList<Player> getPlayers() {
        return players;
    }

    /**
     * Get the current player of the game.
     * @return the current player
     */
    public synchronized Player getCurrentPlayer() {
        return currentPlayer;
    }

    /**
     * See The Future card is played.
     * @return a String which contains the first 3 cards from the top of the deck separated by the symbol ","
     */
    public synchronized String playSeeTheFutureCard() {
        discardCard("See The Future", currentPlayer.getName());
        String result = "";
        Card firstCard;
        Card secondCard;
        Card thirdCard;

        if(!deck.getDrawPile().isEmpty()) {
            firstCard = deck.getDrawPile().peek();
            result += firstCard;
        }
        if(deck.getDrawPile().size() >= 2) {
            secondCard = deck.getDrawPile().get(deck.getDrawPile().size() - 2);
            result += "," + secondCard;
        }
        if(deck.getDrawPile().size() >= 3) {
            thirdCard = deck.getDrawPile().get(deck.getDrawPile().size() - 3);
            result += "," + thirdCard;
        }
        return result;
    }

    /**
     * The Favor card is played.
     * @param playerToStealCardFrom the player who must give a card to the current player
     * @param cardName the name of the card which is given to the current player
     * @requires playerToStealCardFrom != null, cardName != null
     */
    public synchronized void playFavorCard(Player playerToStealCardFrom, String cardName) {
        for(Card card : playerToStealCardFrom.getPlayerHandList()) {
            if(card.toString().contains(cardName)) {
                System.out.println(cardName);
                discardCard(playerToStealCardFrom, playerToStealCardFrom.getPlayerHandList().indexOf(card));
                currentPlayer.addCard(card);
                break;
            }
        }
        discardCard("Favor", currentPlayer.getName());
    }

    /**
     * The Nope card is played.
     * @param playerName the name of the player who played the Nope card.
     * @requires playerName != null
     */
    public synchronized void playNopeCard(String playerName) {
        this.discardCard("Nope", playerName);
    }

    /**
     * This method is called when 2 cards are played in combo.
     * @param playerToStealCardFrom the player from which the current player will take a random card
     * @requires playerToStealCardFrom != null
     */
    public synchronized void playSpecialComboTwoCards(Player playerToStealCardFrom) {
        // get a random card index from the hand of playerToStealCardFrom
        int numberOfCards = playerToStealCardFrom.getPlayerHandList().size();
        int randomCardIndex = (int) (Math.random() * numberOfCards);

        // remove a random card from playerToStealCardFrom and add it to current player's hand
        Card randomCard = playerToStealCardFrom.getPlayerHandList().remove(randomCardIndex);
        currentPlayer.addCard(randomCard);
    }

    /**
     * This method is called when 3 cards are played in combo.
     * @param playerToStealCardFrom the player from which the current player will take a card
     * @param cardToSteal the name of the card which the current player wants to take from playerToStealCardFrom
     * @requires playerToStealCardFrom != null, cardToSteal != null
     */
    public synchronized void playSpecialComboThreeCards(Player playerToStealCardFrom, String cardToSteal) {
        for(Card card : playerToStealCardFrom.getPlayerHandList()) {
            if(card.toString().contains(cardToSteal)) {
                playerToStealCardFrom.getPlayerHandList().remove(card);
                currentPlayer.addCard(card);
                break;
            }
        }
    }

    /**
     * Check if the cards played in a combo are of the same type.
     * @param cards the cards played in combo
     * @param specialCombosActive the value to check if the game is played with special combos
     * @throws E13 if played cards do not have the same type
     * @throws E13 if one of the cards is not a cat card and the game is not played with special combos
     */
    public synchronized void checkCombo(ArrayList<String> cards, boolean specialCombosActive) throws E13 {
        String firstCard = cards.get(0);
        if(!specialCombosActive && !firstCard.equalsIgnoreCase("Rainbow Ralphing Cat") &&
                !firstCard.equalsIgnoreCase("Taco Cat") &&
                !firstCard.equalsIgnoreCase("Cattermelon") &&
                !firstCard.equalsIgnoreCase("Beard Cat") &&
                !firstCard.equalsIgnoreCase("Hairy Potato Cat")) {
            throw new E13();
        }
        for(String card : cards) {
            if(!card.equalsIgnoreCase(firstCard)) {
                throw new E13();
            }
        }
    }

    /**
     * This method is used to check if the current player has a Defuse card in his hand.
     * @return true if the current player has a Defuse card, false otherwise
     */
    public synchronized boolean checkForDefuseCard() {
        return currentPlayer.getPlayerHandString().toLowerCase().contains("defuse");
    }

    /**
     * This method is called when the current player took an Exploding Kitten card from the draw pile, and he does not have a Defuse card.
     * @ensures the player who took the Exploding Kitten card is removed from the game
     * @ensures the variables currentPlayer and currentPlayerIndex are updated
     */
    public synchronized void playExplodingKittenCard() {
        players.remove(currentPlayer);
        if(currentPlayerIndex != 0) {
            currentPlayerIndex -= 1;
        }
        currentPlayer = players.get(currentPlayerIndex);
    }

    /**
     * This method is called when a player took an Exploding Kitten from the draw pile, and he has a Defuse card in his hand.
     * @param response the index where the current player wants to insert the Exploding Kitten card
     * @throws NumberFormatException if the current player does not enter a number for the index (response is not a number)
     */
    public synchronized void playDefuseCard(String response) throws NumberFormatException {
        try {
            int indexToInsertExplodingKitten = Integer.parseInt(response);
            int indexExplodingKittenPlayerHand = currentPlayer.getPlayerHandList().size() - 1;
            deck.getDrawPile().add(indexToInsertExplodingKitten, currentPlayer.getPlayerHandList().get(indexExplodingKittenPlayerHand));
            currentPlayer.getPlayerHandList().remove(indexExplodingKittenPlayerHand);
        } catch (NumberFormatException e) {
            System.out.println("Exception " + e);
        }
        discardCard("Defuse", currentPlayer.getName());
    }

    /**
     * The Shuffle card is played.
     */
    public synchronized void playShuffleCard() {
        deck.shuffleDrawPile();
    }

    /**
     * This method is used to check if a player has a specific card in his hand.
     * @param playerName the player whose hand of cards is checked
     * @param card the card which is searched
     * @return true if the player has the card in his hand, false otherwise
     */
    public synchronized boolean hasCard(String playerName, String card) {
        for(Player player : players) {
            if(player.getName().equals(playerName)) {
                if(player.getPlayerHandString().contains(card)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * This method is used to check if a player has more cards of a specific type in his hand.
     * @param playerName the player whose hand of cards is checked
     * @param cards the cards which are searched
     * @return true if the player has the cards in his hand, false otherwise
     */
    public synchronized boolean hasCards(String playerName, String cards) {
        String[] cardsArray = cards.split(",");
        int cardsPlayed = cardsArray.length;
        int countCardsPlayerHand = 0;

        for(Player player : players) {
            if(player.getName().equals(playerName)) {
                for(Card card : player.getPlayerHandList()) {
                    if(card.toString().contains(cardsArray[0])) {
                        countCardsPlayerHand += 1;
                    }
                    if(countCardsPlayerHand == cardsPlayed) {
                        return true;
                    }

                }
                break;
            }
        }
        return false;
    }

    /**
     * This method is used to check if any player has a Nope card in his hand.
     * @return true if there is no player with a Nope card in his hand, false otherwise
     */
    public synchronized boolean noNopeCardsAtPlayers() {
        for(Player player : players) {
            if(player.getPlayerHandString().contains("Nope")) {
                return false;
            }
        }
        return true;
    }

    /**
     * A player can discard a card in the discard pile.
     * @param cardToDiscard the name of the card which is discarded
     * @param playerName the name of the player who discards the card
     * @requires cardToDiscard != null, playerName != null
     */
    public synchronized void discardCard(String cardToDiscard, String playerName) {
        for(Player player : players) {
            if(player.getName().equals(playerName)) {
                for(Card card : player.getPlayerHandList()) {
                    if(card.toString().contains(cardToDiscard)) {
                        player.getPlayerHandList().remove(card);
                        deck.addCardToDiscardPile(card);
                        break;
                    }
                }
                break;
            }
        }
    }

    /**
     * Discard multiple cards of the same type from the current player's hand.
     * @param cardTypeToDiscard the type of the cards which the current player will discard
     * @param numberOfCards the number of cards which will be discarded
     * @requires cardTypeToDiscard != null
     */
    public synchronized void discardCards(String cardTypeToDiscard, int numberOfCards) {
        for(int i=0; i<numberOfCards; i++) {
            for(Card card : currentPlayer.getPlayerHandList()) {
                if(card.toString().contains(cardTypeToDiscard)) {
                    currentPlayer.getPlayerHandList().remove(card);
                    deck.addCardToDiscardPile(card);
                    break;
                }
            }
        }
    }

    /**
     * The Skip card is played.
     */
    @Override
    public synchronized void playSkipCard(){
        discardCard("Skip", currentPlayer.getName());
        super.playSkipCard();
    }

    /**
     * Get the names of the players.
     * @return a String which contains the names of the players separated by ","
     */
    public synchronized String getPlayersNames() {
        String playersNames = "";
        for(int i=0; i<players.size(); i++) {
            if(i != players.size() - 1) {
                playersNames += players.get(i).getName() + ",";
            } else {
                playersNames += players.get(i).getName();
            }
        }
        return playersNames;
    }
}
