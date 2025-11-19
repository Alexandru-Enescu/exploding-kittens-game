package exceptions;

/**
 * Exception for the network Exploding Kittens game, thrown if a connection was not established or if the user indicates to exit the program
 * @author Oliver Li, Alexandru-Cristian Enescu
 */
public class E10 extends Exception {
    public E10() {
        super("Exit Program");
    }
}
