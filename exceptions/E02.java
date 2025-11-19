package exceptions;

/**
 * Exception for the network Exploding Kittens game, thrown if a player tries to connect with a name which is already taken by another player.
 * @author Alexandru-Cristian Enescu, Oliver Li
 */
public class E02 extends Exception {
    public E02() {
        super("Name already used");
    }
}
