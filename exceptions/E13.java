package exceptions;

/**
 * Exception for the network Exploding Kittens game, thrown if a player tries to select an element that does not exist from an array / list / set / map.
 * @author Oliver Li, Alexandru-Cristian Enescu
 */
public class E13 extends Exception {
    public E13() {
        super("Element doesn't exist");
    }
}
