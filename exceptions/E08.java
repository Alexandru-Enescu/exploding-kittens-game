package exceptions;

/**
 * Exception for the network Exploding Kittens game, thrown if a player tries to send commands to the sever when it's not their turn
 * @author Oliver Li, Alexandru-Cristian Enescu
 */
public class E08 extends Exception {
    public E08() {
        super("You are not allowed to input now");
    }
}
