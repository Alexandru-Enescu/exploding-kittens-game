package exceptions;

/**
 * Exception for the network Exploding Kittens game, thrown if the protocol is not respected.
 * @author Oliver Li, Alexandru-Cristian Enescu
 */
public class E11 extends Exception {
    public E11() {
        super("Protocol Exception");
    }
}
