package main;
public class Card {

    private int cardValue;


    /**
     * Getter for value of card
     * @return Value of card
     */
    public int getCardValue() {
        return cardValue;
    }


    /**
     * Constructor for Card class
     * @param cardValue Value for the card
     * @throws InvalidCardException Card value is not a positive integer
     */
    public Card(int cardValue) throws InvalidCardException{

        if (cardValue < 1) {
            throw new InvalidCardException(String.format(
                "Card value must be a positive integer. Card Value: ", cardValue));
        }

        this.cardValue = cardValue;
    }

    /**
     * Temporary test before Junit becomes viable
     * @param args
     * @throws InvalidCardException
     */
    public static void main(String[] args) throws InvalidCardException{
        Card card = new Card(5);
        System.out.println(card.getCardValue());
    }

}