package exceptions;

/**
 * Exception for the network Exploding Kittens game, thrown if a player tries to join a lobby which is already full.
 * @author Alexandru-Cristian Enescu, Oliver Li
 */
public class E03 extends Exception {
    public E03() {
        super("LOBBY FULL - Too many players in the lobby");
    }
}
