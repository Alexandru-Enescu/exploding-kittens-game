package network.model;

import local.model.Game;

/**
 * Class which helps to play the Shuffle card in the Exploding Kittens game.
 * @author Alexandru-Cristian Enescu and Oliver Li
 */
public class ShuffleDeck extends Thread {
    private final Game game;
    public ShuffleDeck(Game game) {
        this.game = game;
    }

    @Override
    public void run() {
        while(((NetworkGame) game).isKeepShuffle()) {
            game.playShuffleCard();
            System.out.println(game.getDeck().getDrawPile());
        }
    }
}
