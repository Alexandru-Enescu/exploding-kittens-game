package exceptions;

/**
 * Exception for the Exploding Kittens game, thrown if a player wants to play more cards which do not have the same type.
 * @author Alexandru-Cristian Enescu and Oliver Li
 */
public class NotMatchingCardsException extends Exception {
    public NotMatchingCardsException(String message) {
        super(message);
    }
}
