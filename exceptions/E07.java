package exceptions;

/**
 * Exception for the network Exploding Kittens game, thrown if a player tries to play a card that he does not have.
 * @author Oliver Li, Alexandru-Cristian Enescu
 */
public class E07 extends Exception {
    public E07() {
        super("Card not in hand");
    }
}
