package exceptions;

/**
 * Exception for the network Exploding Kittens game, thrown if a player tries to remove a computer player when there are not any connected
 * @author Alexandru-Cristian Enescu, Oliver Li
 */
public class E06 extends Exception {
    public E06() {
        super("There are not any computer players connected");
    }
}
