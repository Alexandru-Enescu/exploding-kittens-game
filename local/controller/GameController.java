package local.controller;

import local.model.LocalGame;
import local.view.GameView;
import protocol.ProtocolCommands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

/**
 * Class to represent the controller of the local game which takes input from the players and uses it to make changes in the model.
 * @author Oliver Li and Alexandru-Cristian Enescu
 */
public class GameController {
    private LocalGame localGame;
    private final GameView gameView;
    private final Scanner SCANNER = new Scanner(System.in);

    /**
     * Initialize the user interface and create a new game.
     */
    public GameController() {
        this.gameView = new GameView();
    }

    /**
     * Get the names of the players.
     * @return a list with the names of the human players
     */
    public ArrayList<String> getPlayersNames() {
        gameView.askPlayersNames();
        String[] playersNames = SCANNER.nextLine().split(ProtocolCommands.ELEMENT_SEPARATOR);
        if(playersNames.length > 5) {
            gameView.maximumFivePlayers();
            return null;
        }
        return new ArrayList<>(Arrays.asList(playersNames));
    }

    /**
     * This method is called when the player is asked to enter input.
     * @return player's input
     */
    public String getPlayerInput() {
        return SCANNER.nextLine();
    }

    /**
     * Starts the game and continues until one player has won.
     */
    public void startGame() {
        ArrayList<String> playersNames = getPlayersNames();
        if(playersNames != null) {
            localGame = new LocalGame(playersNames, gameView, this);
            localGame.setUpGame();
            localGame.playGame();
        }
    }

    public static void main(String[] args) {
        GameController gameController = new GameController();
        gameController.startGame();
    }
}
