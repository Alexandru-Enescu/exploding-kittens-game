package exceptions;

/**
 * Exception for the network Exploding Kittens game, thrown if a player enters a command which is not in the protocol.
 * @author Alexandru-Cristian Enescu, Oliver Li
 */
public class E01 extends Exception {
    public E01() {
        super("Unknown Command");
    }
}
