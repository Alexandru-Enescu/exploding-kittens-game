package local.view;

import local.model.Card;
import local.model.CardType;
import local.model.Player;
import java.util.ArrayList;

import static local.view.ANSI.*;

/**
 * TUI for the local Exploding Kittens game.
 * @author Alexandru-Cristian Enescu and Oliver Li
 */
public class GameView {

    /**
     * Ask players to enter their names.
     */
    public void askPlayersNames() {
        System.out.println(YELLOW_BOLD + """
      
        --------------- Exploding Kittens ----------------
        Instructions:
        To play a card write its index, to end your turn type "draw".
        To play more cards, write each index separated by the symbol ",".
        To begin, type your names separated by the symbol ",".
        """ + RESET);
    }

    /**
     * Ask a player to play his turn.
     * @param playerName the player whose turn it is
     * @requires playerName != null
     */
    public void askToPlayTurn(String playerName, ArrayList<Card> playerHand) {
        System.out.println(YELLOW_BOLD + "\n" + playerName + """
        , it's your turn to play!
        Which card would you like to play?
        Your cards are:""" + RESET);

        for(int i=0; i<playerHand.size(); i++) {
            System.out.println(YELLOW_BOLD + (i+1) + ". " + RESET + playerHand.get(i));
        }
    }

    /**
     * Print the names of the players in the order they will play.
     * @param players the players of the game
     */
    public void printPlayersNames(ArrayList<Player> players) {
        String playersNames = "\nPlayers will play in the following order: ";
        for(Player player : players) {
            Player lastPlayer = players.get(players.size()-1);
            if(player.equals(lastPlayer)) {
                playersNames += player.getName() + ".";
            } else {
                playersNames += player.getName() + ", ";
            }
        }
        System.out.println(YELLOW_BOLD + playersNames + RESET);
    }

    public void shuffleCardPlayed(String playerName) {
        System.out.println(YELLOW_BOLD + playerName + " played a 'Shuffle' card. The deck has been shuffled." + RESET);
    }

    public void seeTheFutureCardPlayed(String playerName, Card firstCard, Card secondCard, Card thirdCard) {
        System.out.println(YELLOW_BOLD + playerName + """
        played a 'See The Future' card.
        The first 3 cards from the draw pile are: """ + firstCard + ", " + secondCard + YELLOW_BOLD + " and " + thirdCard + "." + RESET);
    }

    public void attackCardAnnouncePlayers(String playerName, String nextPlayerName) {
        System.out.println(YELLOW_BOLD + playerName + " has played an 'Attack' card on " + nextPlayerName + ". " + RESET);
    }

    public void attackCardAnnounceRemainingTurns(int remainingTurnsToPlay) {
        System.out.print(YELLOW_BOLD + "He has to play " + remainingTurnsToPlay + " turns." + RESET);
    }

    public void askToPlayNopeCard() {
        System.out.println(YELLOW_BOLD + "If any player wants to play a \"Nope\" card, type the player's name. Otherwise, type \"c\" to continue." + RESET);
    }

    public void nopeCardPlayed(String playerName, String currentPlayerName, ArrayList<Card> playedCards, int numberNopeCardsPlayed) {
        String result;

        // if the current player played one card
        if(playedCards.size() == 1) {
            if (numberNopeCardsPlayed % 2 == 0) {
                result = playerName + " has played a 'Nope' card on another 'Nope' card to create a 'Yup'. The " + playedCards.get(0) +
                        YELLOW_BOLD + " card played by " + currentPlayerName + " will be played.";
            } else {
                result = playerName + " has played a 'Nope' card. The " + playedCards.get(0) + YELLOW_BOLD + " card played by " + currentPlayerName + " will not be played.";
            }

        // if the current player played more cards
        } else {
            if (numberNopeCardsPlayed % 2 == 0) {
                result = playerName + " has played a 'Nope' card on another 'Nope' card to create a 'Yup'. The " + playedCards.size() + " " + playedCards.get(0) +
                        YELLOW_BOLD + " cards played by " + currentPlayerName + " will be played.";
            } else {
                result = playerName + " has played a 'Nope' card. The " + playedCards.size() + " " + playedCards.get(0) + YELLOW_BOLD + " cards played by " +
                        currentPlayerName + " will not be played.";
            }
        }
        System.out.println(YELLOW_BOLD + result + RESET);
    }

    public void invalidPlayerName(String playerName) {
        System.out.println(WHITE_BOLD_BRIGHT + playerName + " is not a player of the game." + RESET);
    }

    public void nopeCardNotFound(String playerName) {
        System.out.println(WHITE_BOLD_BRIGHT + playerName + " does not have a \"Nope\" card." + RESET);
    }

    public void announcePlayedCards(String playerName, ArrayList<Card> cards) {
        String result;
        if(cards.size() == 1) {
            result = YELLOW_BOLD + playerName + " wants to play a " + RESET + cards.get(0) + YELLOW_BOLD + " card." + RESET;
        } else {
            result = YELLOW_BOLD + playerName + " wants to play " + cards.size() + " " + RESET + cards.get(0) + YELLOW_BOLD + " cards." + RESET;
        }
        System.out.println(result);
    }

    public void askToPlayDefuseCard(String playerName) {
        System.out.println(YELLOW_BOLD + playerName + ", you just took an 'Exploding Kitten' card from the draw pile. To continue the game you have to play a 'Defuse' card, otherwise type \"I cannot defuse\"." + RESET);
    }

    public void defuseCardNotFound(String playerName) {
        System.out.println(WHITE_BOLD_BRIGHT + playerName + ", the card you entered is not a 'Defuse' card. Type again, please." + RESET);
    }

    public void defuseCardPlayed(String playerName, int drawPileSize) {
        System.out.println(YELLOW_BOLD + playerName + " has played a 'Defuse' card. " + playerName + " type the index where you want to insert the Exploding Kitten card. There are " +
                drawPileSize + " cards in the draw pile." + RESET);
    }

    public void favorCardPlayed(String nameCurrentPlayer, String namePlayerToTakeCardFrom, Card card) {
        System.out.println(YELLOW_BOLD + namePlayerToTakeCardFrom + " has given a " + card + YELLOW_BOLD + " card to " + nameCurrentPlayer + "." + RESET);
    }

    public void printWrongIndexToInsertExplodingKitten(String playerName, String wrongIndex, int drawPileSize) {
        System.out.println(WHITE_BOLD_BRIGHT + playerName + ", you cannot insert the 'Exploding Kitten' card at index " + wrongIndex + ". The draw pile has " + drawPileSize + " cards." + RESET);
    }

    public void printWrongInputInsertExplodingKitten(String playerName) {
        System.out.println(WHITE_BOLD_BRIGHT + playerName + ", type the index where you want to insert the Exploding Kitten card, please." + RESET);
    }

    public void printPlayerOutOfTheGame(String playerName) {
        System.out.println(YELLOW_BOLD + playerName + " could not defuse the 'Exploding Kitten' card, he is now out of the game." + RESET);
    }

    public void printInsertExplodingKitten(String playerName) {
        System.out.println(YELLOW_BOLD + playerName + " put the 'Exploding Kitten' card somewhere in the draw pile." + RESET);
    }

    public void isWinner(String playerName) {
        System.out.println(YELLOW_BOLD + playerName + " has won the game!" + RESET);
    }

    public void printWrongInput(String playerName) {
        System.out.println(WHITE_BOLD_BRIGHT + playerName + ", type the index of the card you want to play or \"draw\" to end your turn." + RESET);


    }

    public void printWrongCardIndex(String playerName, String wrongCardIndex) {
        System.out.println(WHITE_BOLD_BRIGHT + playerName + ", you do not have any card with index " + wrongCardIndex + "." + RESET);
    }

    public void printNotMatchingCards(String playerName) {
        System.out.println(WHITE_BOLD_BRIGHT + playerName + ", the cards you played do not have the same type." + RESET);
    }

    public void twoMatchingCardsPlayed(String nameCurrentPlayer, String namePlayerToStealCardFrom) {
        System.out.println(YELLOW_BOLD + nameCurrentPlayer + " has stolen a random card from " + namePlayerToStealCardFrom + "." + RESET);
    }

    public void threeMatchingCardsPlayed(String nameCurrentPlayer, String namePlayerToStealCardFrom, Card cardToSteal) {
        System.out.println(YELLOW_BOLD + nameCurrentPlayer + " has stolen a " + cardToSteal + YELLOW_BOLD + " card from " + namePlayerToStealCardFrom + "." + RESET);

    }

    public void threeMatchingCardsPlayed(String namePlayerToStealCardFrom, CardType cardType) {
        System.out.println(YELLOW_BOLD + namePlayerToStealCardFrom + " does not have a " + cardType + " card." + RESET);
    }

    public void askPlayerNameToStealCardFrom(String nameCurrentPlayer) {
        System.out.println(YELLOW_BOLD + nameCurrentPlayer + ", type the name of the player from which you want to steal a card." + RESET);
    }

    public void askNameCardToSteal(String nameCurrentPlayer, String namePlayerToStealCardFrom) {
        System.out.println(YELLOW_BOLD + nameCurrentPlayer + ", type the name of the card you want to steal from " + namePlayerToStealCardFrom + "." + RESET);
    }

    public void askPlayerNameToTakeCardFrom(String nameCurrentPlayer) {
        System.out.println(YELLOW_BOLD + nameCurrentPlayer + ", type the name of the player from which you want to take one card." + RESET);
    }

    public void askIndexCardToGive(String nameCurrentPlayer, String namePlayerToTakeCardFrom) {
        System.out.println(YELLOW_BOLD + namePlayerToTakeCardFrom + ", you must give a card to " + nameCurrentPlayer + ". Type the index of the card, please." + RESET);
    }

    public void maximumFivePlayers() {
        System.out.println(WHITE_BOLD_BRIGHT + "The game can be played with maximum 5 players." + RESET);
    }
}
