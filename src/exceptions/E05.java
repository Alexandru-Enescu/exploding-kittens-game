package exceptions;

/**
 * Exception for the network Exploding Kittens game, thrown if a player tries to start a game while the minimum number of players is not met.
 * @author Alexandru-Cristian Enescu, Oliver Li
 */
public class E05 extends Exception {
    public E05() {
        super("Not enough people to start a game");
    }
}
