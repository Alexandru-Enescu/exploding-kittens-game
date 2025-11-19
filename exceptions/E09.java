package exceptions;

/**
 * Exception for the network Exploding Kittens game, thrown if a player tries to enter an existing server with other flags
 * than those used by the first player who joined the server
 * @author Oliver Li, Alexandru-Cristian Enescu
 */
public class E09 extends Exception {
    public E09() {
        super("Flags don’t match");
    }
}
