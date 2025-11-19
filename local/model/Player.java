package local.model;

import java.util.ArrayList;

/**
 * Player class to represent a local player of the Exploding Kittens game.
 * @author Alexandru-Cristian Enescu and Oliver Li
 */
public class Player {
    private String name;
    private ArrayList<Card> playerHand;

    public Player(String name) {
        this.name = name;
        this.playerHand = new ArrayList<>();
    }

    /**
     * Get the name of the player.
     * @return name of the player
     */
    public String getName() {
        return this.name;
    }

    /** Get the cards the player has in his hand
     * @return a list with player's cards
     */
    public ArrayList<Card> getPlayerHandList() {
        return this.playerHand;
    }

    /** Get the cards the player has in his hand
     * @return a String which contains player's cards separated by " , "
     */
    public String getPlayerHandString() {
        ArrayList<Card> playerHandList = this.getPlayerHandList();
        String playerHandString = "";
        for(Card card : playerHandList) {
            if(playerHandList.indexOf(card) != playerHandList.size() - 1) {
                playerHandString += card + ",";
            } else {
                playerHandString += card;
            }
        }
        return playerHandString;
    }

    /**
     * Add a card to the hand of the player.
     * @param card the card to be added
     */
    public void addCard(Card card) {
        this.getPlayerHandList().add(card);
    }

    @Override
    public String toString() {
        return this.getName();
    }
}
