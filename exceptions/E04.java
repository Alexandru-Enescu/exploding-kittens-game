package exceptions;

/**
 * Exception for the network Exploding Kittens game, thrown if a player tries to connect to a server which is already full.
 * @author Alexandru-Cristian Enescu, Oliver Li
 */
public class E04 extends Exception {
    public E04() {
        super("SERVER FULL - Too many players connected on the server");
    }
}
