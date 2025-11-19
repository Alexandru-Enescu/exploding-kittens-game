package local.model;

import local.controller.GameController;
import exceptions.CardNotFoundException;
import exceptions.NotMatchingCardsException;
import exceptions.PlayerNotFoundException;
import local.view.GameView;
import java.util.ArrayList;

/**
 * Class which holds a local Exploding Kittens game.
 * @author Oliver Li and Alexandru-Cristian Enescu
 */
public class LocalGame extends Game {
    private final GameView gameView;
    private final GameController gameController;

    /**
     * Create a local Exploding Kittens game.
     * Initialize the deck, the players, the view and the controller of the game.
     * @param playersNames the names of the local players added to the game
     * @param gameView the view of the game
     * @param gameController the controller of the game
     */
    public LocalGame(ArrayList<String> playersNames, GameView gameView, GameController gameController) {
        super(playersNames);
        this.gameView = gameView;
        this.gameController = gameController;
    }

    /**
     * Set up the local Exploding Kittens game.
     * @ensures give 1 defuse card and 7 random cards to each player
     */
    @Override
    public void setUpGame() {
        super.setUpGame();
        gameView.printPlayersNames(players);
    }

    /**
     * Control the direction of the game.
     * If a player wants to end his turn <code>currentPlayer</code> and <code>currentPlayerIndex</code> are updated.
     * If a player was attacked <code>additionalTurnsToPlay</code> is checked and updated.
     * A player can play any amount of cards, but his turn will end only when he draws a card.
     * After each card played, ask all players to play Nope cards and check if the action must be stopped.
     */
    public void playGame() {
        while(!gameOver()) {
            // ask current player to play his turn
            gameView.askToPlayTurn(currentPlayer.getName(), currentPlayer.getPlayerHandList());
            String currentPlayerInput = gameController.getPlayerInput();

            // if current player wants to draw a card
            if(currentPlayerInput.equalsIgnoreCase("draw")) {

                // if current player draws an Exploding Kitten card
                if(this.drawCard(currentPlayer).getCardType().equals(CardType.EXPLODING_KITTEN)) {
                    this.drawExplodingKittenCard();
                    continue;
                }

                // change turn to next player and check if the current player was attacked, if yes, change back turn to current player
                this.changeTurnToNextPlayer();
                this.checkAttackOn();

            // if the current player plays one or more cards, add the cards to a list
            } else {
                ArrayList<Card> playedCards;
                try {
                    playedCards = this.getPlayedCards(currentPlayerInput);
                } catch (NumberFormatException e) {
                    gameView.printWrongInput(currentPlayer.getName());
                    continue;
                } catch (IndexOutOfBoundsException e) {
                    gameView.printWrongCardIndex(currentPlayer.getName(), currentPlayerInput);
                    continue;
                } catch (NotMatchingCardsException e) {
                    gameView.printNotMatchingCards(currentPlayer.getName());
                    continue;
                }

                // announce the cards the current player wants to play and get the number of Nope cards played after
                gameView.announcePlayedCards(currentPlayer.getName(), playedCards);
                int nopeCardsPlayed = waitForPlayingNopeCards(playedCards);

                // if Nope cards were not played or if an even number of Nope cards were played, the action card can be played
                if(nopeCardsPlayed % 2 == 0) {

                    // if the current player played one card
                    if(playedCards.size() == 1) {
                        this.playCard(playedCards.get(0));

                    // if the current player played more cards
                    } else {
                        this.playCards(playedCards);
                    }

                // if an odd number of Nope cards were played after an action card, the action card is not played, but it's discarded from the current player's hand
                } else {
                    for(Card card : playedCards) {
                        this.discardCard(card);
                    }
                }
            }
        }
    }

    /**
     * Get the cards played by the current player.
     * @param playerInput the current player input
     * @return played cards
     */
    public ArrayList<Card> getPlayedCards(String playerInput) throws NumberFormatException, IndexOutOfBoundsException, NotMatchingCardsException {
        ArrayList<Card> playedCards = new ArrayList<>();

        // array which contains the index of each card played
        String[] playerInputArray = playerInput.split(",");

        // add each played card to a list and return it
        for(String cardIndex : playerInputArray) {
            playedCards.add(this.getPlayedCard(cardIndex));
        }

        // check if played cards have the same type
        CardType cardType = playedCards.get(0).getCardType();
        for(Card card : playedCards) {
            if(!card.getCardType().equals(cardType)) {
                throw new NotMatchingCardsException("The played cards do not have the same type.");
            }
        }
        return playedCards;
    }

    /**
     * This method is called each time after a player wants to play an action card, in order to give all players the chance to play Nope cards.
     * Each player can play as many Nope cards as he wants and the game will continue when all players confirm that they do not want to play a Nope card.
     * @param playedCards the cards played by the current player
     * @return the number of Nope cards which were played one after the other
     */
    public int waitForPlayingNopeCards(ArrayList<Card> playedCards) {
        int nopeCardsPlayed = 0;
        boolean confirmContinueGame = false;

        while(!confirmContinueGame) {
            gameView.askToPlayNopeCard();
            String answerNopeCardQuestion = gameController.getPlayerInput();

            // if players want to continue the game
            if(answerNopeCardQuestion.equalsIgnoreCase("c")) {
                confirmContinueGame = true;

            // if a player played a Nope card
            } else {
                try {
                    this.playNopeCard(answerNopeCardQuestion);
                    nopeCardsPlayed += 1;
                    gameView.nopeCardPlayed(answerNopeCardQuestion, currentPlayer.getName(), playedCards, nopeCardsPlayed);
                } catch (PlayerNotFoundException e) {
                    gameView.invalidPlayerName(answerNopeCardQuestion);
                } catch (CardNotFoundException e) {
                    gameView.nopeCardNotFound(answerNopeCardQuestion);
                }
            }
        }
        return nopeCardsPlayed;
    }

    /**
     * Get the card which the current player wants to play.
     * @param playerInput the index of the played card
     * @return the card played by the current player
     */
    public Card getPlayedCard(String playerInput) throws NumberFormatException, IndexOutOfBoundsException {
        int indexCardToPlay = Integer.parseInt(playerInput) - 1;
        return currentPlayer.getPlayerHandList().get(indexCardToPlay);
    }

    /**
     * Add the card which was played to the discard pile, remove it from player's hand and play the card.
     * @param card the card which is played
     * @requires card != null
     */
    public void playCard(Card card) {
        this.discardCard(card);
        switch(card.getCardType()) {
            case ATTACK -> playAttackCard();
            case FAVOR -> playFavorCard();
            case SHUFFLE -> playShuffleCard();
            case SKIP -> playSkipCard();
            case SEE_THE_FUTURE -> playSeeTheFutureCard();
        }
    }

    /**
     * This method is used when cards in combo were played.
     * It asks the current player to enter the name of the player from which he wants to steal a card.
     * If 2 cards were played in combo, the current player will receive a random card from the player whose name he entered.
     * If 3 cards were played in combo, the current player is asked to enter the name of the card he wants to steal.
     * @param cards the cards which were played
     * @requires cards != null
     */
    public void playCards(ArrayList<Card> cards) {
        Player playerToStealCardFrom = null;
        boolean correctInput = false;

        for(Card card : cards) {
            this.discardCard(card);
        }
        gameView.askPlayerNameToStealCardFrom(currentPlayer.getName());

        while(!correctInput) {
            String playerInput = gameController.getPlayerInput();

            // find the player from which the current player will steal a card
            for(Player player : players) {
                if(player.getName().equals(playerInput)) {
                    playerToStealCardFrom = player;
                    correctInput = true;
                }
            }
            if(playerToStealCardFrom == null) {
                gameView.invalidPlayerName(playerInput);
            }
        }

        // if the player played 2 matching cards
        if(cards.size() == 2) {

            // get a random card index from the hand of playerToStealCardFrom
            int numberOfCards = playerToStealCardFrom.getPlayerHandList().size();
            int randomCardIndex = (int) (Math.random() * numberOfCards);

            // remove a random card from playerToStealCardFrom and add it to current player's hand
            Card randomCard = playerToStealCardFrom.getPlayerHandList().remove(randomCardIndex);
            currentPlayer.addCard(randomCard);
            gameView.twoMatchingCardsPlayed(currentPlayer.getName(), playerToStealCardFrom.getName());
        }

        // if the player played 3 matching cards
        else {
            CardType cardTypeToSteal = null;
            correctInput = false;
            gameView.askNameCardToSteal(currentPlayer.getName(), playerToStealCardFrom.getName());

            while(!correctInput) {
                String playerInput = gameController.getPlayerInput().replace(" ", "_");

                // check if the player entered a correct card type
                for(CardType cardType : CardType.values()) {
                    if(cardType.name().equalsIgnoreCase(playerInput)) {
                        cardTypeToSteal = cardType;
                        correctInput = true;
                        break;
                    }
                }
                if(cardTypeToSteal == null) {
                    gameView.askNameCardToSteal(currentPlayer.getName(), playerToStealCardFrom.getName());
                }
            }

            int numberOfCardsBeforeCheck = playerToStealCardFrom.getPlayerHandList().size();
            for(Card card : playerToStealCardFrom.getPlayerHandList()) {
                        if(card.getCardType().equals(cardTypeToSteal)) {
                            playerToStealCardFrom.getPlayerHandList().remove(card);
                            currentPlayer.addCard(card);
                    gameView.threeMatchingCardsPlayed(currentPlayer.getName(), playerToStealCardFrom.getName(), card);
                    break;
                }
            }
            if(playerToStealCardFrom.getPlayerHandList().size() == numberOfCardsBeforeCheck) {
                gameView.threeMatchingCardsPlayed(playerToStealCardFrom.getName(), cardTypeToSteal);
            }
        }
    }

    /**
     * The "Attack" card is played.
     * If a player attacks for the first time, the next player has 2 turns to play.
     * If an attacked player attacks again, the next player has to play the number of remaining turns plus 2 additional turns
     * @ensures <code>additionalTurnsToPlay</code> is updated, its value is set to 1 if an attack just started, otherwise it is increased by 2
     */
    @Override
    public void playAttackCard(){
        Player nextPlayer = players.get(this.getNextPlayerIndex());
        gameView.attackCardAnnouncePlayers(currentPlayer.getName(), nextPlayer.getName());
        super.playAttackCard();
        gameView.attackCardAnnounceRemainingTurns(this.additionalTurnsToPlay + 1);
    }

    /**
     * The "Favor" card is played.
     */
    public void playFavorCard(){
        boolean correctInput = false;
        Player playerToTakeCardFrom = null;
        gameView.askPlayerNameToTakeCardFrom(currentPlayer.getName());

        while(!correctInput) {
            String playerInput = gameController.getPlayerInput();

            // find the player who must give a card to the current player
            for(Player player : players) {
                if(player.getName().equals(playerInput)) {
                    playerToTakeCardFrom = player;
                    correctInput = true;
                }
            }
            if(playerToTakeCardFrom == null) {
                gameView.invalidPlayerName(playerInput);
            }
        }

        gameView.askIndexCardToGive(currentPlayer.getName(), playerToTakeCardFrom.getName());
        correctInput = false;

        while(!correctInput) {
            String playerInput = gameController.getPlayerInput();
            try {
                int cardIndex = Integer.parseInt(playerInput) - 1;
                Card card = playerToTakeCardFrom.getPlayerHandList().remove(cardIndex);
                currentPlayer.addCard(card);
                gameView.favorCardPlayed(currentPlayer.getName(), playerToTakeCardFrom.getName(), card);
                correctInput = true;
            } catch (NumberFormatException e) {
                gameView.askIndexCardToGive(currentPlayer.getName(), playerToTakeCardFrom.getName());
            } catch (IndexOutOfBoundsException e) {
                gameView.printWrongCardIndex(playerToTakeCardFrom.getName(), playerInput);
            }
        }
    }

    /**
     * The "Nope" card is played.
     * @param playerName the name of the player who wants to play a Nope card
     * @throws PlayerNotFoundException if there is no player found with the given player name
     * @throws CardNotFoundException if the player does not have a Nope card in his hand of cards
     */
    public void playNopeCard(String playerName) throws PlayerNotFoundException, CardNotFoundException {
        Player playerWhoPlaysNopeCard = null;
        int indexNopeCard = -1;

        // get the player who wants to play a "Nope" card
        for(Player player : players) {
            if(player.getName().equals(playerName)) {
                playerWhoPlaysNopeCard = player;
                break;
            }
        }

        // check if a player was found with the entered player name
        if(playerWhoPlaysNopeCard == null) {
            throw new PlayerNotFoundException(playerName + " is not a player of the game.");
        }

        // get the index of the "Nope" card
        for(Card card : playerWhoPlaysNopeCard.getPlayerHandList()) {
            if(card.getCardType().equals(CardType.NOPE)) {
                indexNopeCard = playerWhoPlaysNopeCard.getPlayerHandList().indexOf(card);
                break;
            }
        }

        // check if the player has a "Nope" card
        if(indexNopeCard == -1) {
            throw new CardNotFoundException("Player " + playerName + " does not have a \"Nope\" card.");
        }

        // add the "Nope" card in the discard pile and remove it from player's hand
        this.discardCard(playerWhoPlaysNopeCard, indexNopeCard);
    }

    /**
     * The "Shuffle" card is played.
     */
    @Override
    public void playShuffleCard() {
        deck.shuffleDrawPile();
        gameView.shuffleCardPlayed(currentPlayer.getName());
    }

    /**
     * The "See The Future" card is played.
     * @ensures the first 3 cards from the top of the draw pile are shown to the current player
     */
    public void playSeeTheFutureCard(){
        Card firstCard = deck.getDrawPile().peek();
        Card secondCard = deck.getDrawPile().get(deck.getDrawPile().size() - 2);
        Card thirdCard = deck.getDrawPile().get(deck.getDrawPile().size() - 3);
        gameView.seeTheFutureCardPlayed(currentPlayer.getName(), firstCard, secondCard, thirdCard);
    }

    /**
     * This method is called when the current player draws an Exploding Kitten card.
     * @ensures if player does not have a Defuse card, he is removed from the list of players and <code>currentPlayer</code> is updated
     * @ensures if player has a Defuse card, he can insert the Exploding Kitten card back in the draw pile anywhere he would like and his turn is over
     */
    public void drawExplodingKittenCard() {
        gameView.askToPlayDefuseCard(currentPlayer.getName());
        boolean correctInput = false;

        while(!correctInput) {
            String playerInput = gameController.getPlayerInput();

            // if the player does not have a Defuse card
            if(playerInput.equalsIgnoreCase("I cannot defuse")) {
                gameView.printPlayerOutOfTheGame(currentPlayer.getName());
                players.remove(currentPlayer);
                if(currentPlayerIndex == players.size()) {
                    currentPlayerIndex = 0;
                }
                currentPlayer = players.get(currentPlayerIndex);
                if(gameOver()) {
                    gameView.isWinner(currentPlayer.getName());
                }
                correctInput = true;

            // if the player wants to play a Defuse card
            } else {

                // if the card played by the player is a Defuse card
                if(playDefuseCard(playerInput)) {
                    gameView.printInsertExplodingKitten(currentPlayer.getName());
                    this.changeTurnToNextPlayer();
                    this.checkAttackOn();
                    correctInput = true;

                // if the card played by the player is not a Defuse card
                } else {
                    gameView.defuseCardNotFound(currentPlayer.getName());
                }
            }
        }
    }

    /**
     * This method is called when the player is asked to play a Defuse card.
     * @param playerInput the index of the played card
     * @ensures if the played card is a Defuse card, the current player can insert the Exploding Kitten card back into the draw pile at any index
     * @ensures the current player discards the Defuse card played into the discard pile
     * @return true if the played card is a Defuse card, false otherwise
     */
    public boolean playDefuseCard(String playerInput) {
        Card playedCard;
        try {
            playedCard = this.getPlayedCard(playerInput);
        } catch (NumberFormatException e) {
            return false;
        }

        // if the player played a Defuse card
        if(playedCard.getCardType().equals(CardType.DEFUSE)) {
            this.discardCard(playedCard);
            int drawPileSize = deck.getDrawPile().size();
            gameView.defuseCardPlayed(currentPlayer.getName(), drawPileSize);
            boolean correctInput = false;

            // the player is asked to insert the Exploding Kitten card back into the draw pile at any index
            while(!correctInput) {
                String inputIndex = gameController.getPlayerInput();
                try {
                    int indexToInsertExplodingKitten = Integer.parseInt(inputIndex);
                    int indexExplodingKittenPlayerHand = currentPlayer.getPlayerHandList().size() - 1;
                    deck.getDrawPile().add(indexToInsertExplodingKitten, currentPlayer.getPlayerHandList().get(indexExplodingKittenPlayerHand));
                    currentPlayer.getPlayerHandList().remove(indexExplodingKittenPlayerHand);
                    correctInput = true;
                } catch (NumberFormatException e) {
                    gameView.printWrongInputInsertExplodingKitten(currentPlayer.getName());
                } catch (IndexOutOfBoundsException e) {
                    gameView.printWrongIndexToInsertExplodingKitten(currentPlayer.getName(), inputIndex, drawPileSize);
                }
            }
            return true;

        // if the player did not play a Defuse card
        } else {
            return false;
        }
    }
}
