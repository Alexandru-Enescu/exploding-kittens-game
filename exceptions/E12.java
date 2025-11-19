package exceptions;

/**
 * Exception for the network Exploding Kittens game, thrown if IO errors occur.
 * @author Oliver Li, Alexandru-Cristian Enescu
 */
public class E12 extends Exception {
    public E12() {
        super("Server Unavailable Exception");
    }
}
