package local.model;

import static local.model.CardType.*;
import static local.view.ANSI.*;

/**
 * Class to represent a card in the Exploding Kittens game.
 * @author Alexandru-Cristian Enescu and Oliver Li
 */
public class Card {
    private final CardType cardType;

    /**
     * Create a card and initialize its type.
     * @param cardType the type of the card
     */
    public Card(CardType cardType) {
        this.cardType = cardType;
    }

    /**
     * Check what type does a card have.
     * @return the type of the card
     */
    public CardType getCardType() {
        return cardType;
    }

    @Override
    public String toString() {
        String cardName = String.valueOf(this.cardType);
        String result = null;
        switch (cardName){
            case "EXPLODING_KITTEN"->{
                result = RED_BOLD_BRIGHT+"Exploding Kitten"+RESET;
            }
            case "DEFUSE" ->{
                result = PURPLE_BOLD_BRIGHT +"Defuse"+RESET;
            }
            case "ATTACK" ->{
                result = YELLOW_BOLD_BRIGHT+"Attack"+RESET;
            }
            case "FAVOR" ->{
                result = CYAN_BOLD_BRIGHT+"Favor"+RESET;
            }
            case "NOPE" ->{
                result = BLACK_BOLD_BRIGHT+"Nope"+RESET;
            }
            case "SKIP" ->{
                result = BLUE_BOLD_BRIGHT+"Skip"+RESET;
            }
            case "SHUFFLE" ->{
                result = GREEN_BOLD_BRIGHT+"Shuffle"+RESET;
            }
            case "SEE_THE_FUTURE" ->{
                result = WHITE_BOLD_BRIGHT+"See The Future"+RESET;
            }
            case "TACO_CAT" ->{
                result = RED_BOLD+"Taco Cat"+RESET;
            }
            case "HAIRY_POTATO_CAT" ->{
                result = PURPLE_BOLD+"Hairy Potato Cat"+RESET;
            }
            case "RAINBOW_RALPHING_CAT" ->{
                result = YELLOW_BOLD+"Rainbow Ralphing Cat"+RESET;
            }
            case "BEARD_CAT" ->{
                result = CYAN_BOLD+"Beard Cat"+RESET;
            }
            case "CATTERMELON" ->{
                result = BLUE_BOLD+"Cattermelon"+RESET;
            }

        }
        return result;
    }


    //Test Card
    public static void main(String[] args) {
        Card card1 = new Card(CardType.EXPLODING_KITTEN);
        Card card2 = new Card(DEFUSE);
        Card card3 = new Card(ATTACK);
        Card card4 = new Card(FAVOR);
        Card card5 = new Card(NOPE);
        Card card6 = new Card(SKIP);
        Card card7 = new Card(SHUFFLE);
        Card card8 = new Card(SEE_THE_FUTURE);
        Card card9 = new Card(TACO_CAT);
        Card card10 = new Card(HAIRY_POTATO_CAT);
        Card card11 = new Card(RAINBOW_RALPHING_CAT);
        Card card12 = new Card(BEARD_CAT);
        Card card13 = new Card(CATTERMELON);

        System.out.println(card1);
        System.out.println(card2);
        System.out.println(card3);
        System.out.println(card4);
        System.out.println(card5);
        System.out.println(card6);
        System.out.println(card7);
        System.out.println(card8);
        System.out.println(card9);
        System.out.println(card10);
        System.out.println(card11);
        System.out.println(card12);
        System.out.println(card13);
    }
}
