package exceptions;

/**
 * Exception for the Exploding Kittens game, thrown if an invalid player name is entered when a player wants to play a "Nope" card.
 * @author Alexandru-Cristian Enescu and Oliver Li
 */
public class PlayerNotFoundException extends Exception {
    public PlayerNotFoundException(String message) {
        super(message);
    }
}
