package exceptions;

/**
 * Exception for the Exploding Kittens game, thrown if a player enters an invalid card index or if the draw pile is empty.
 * @author Oliver Li and Alexandru-Cristian Enescu
 */
public class CardNotFoundException extends Exception {
    public CardNotFoundException(String message) {
        super(message);
    }
}
