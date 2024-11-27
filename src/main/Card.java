package main;
public class Card {

    private int cardValue;
    private int weighting = 0;
    private final int weightingMultiplier = 2;
    // Base weighting for when a card is a preferred or not preferred by player
    private final int preferredWeighting = 0;
    private final int nonPreferredWeighting = 1;


    /**
     * Getter for value of card
     * @return Value of card
     */
    public int getCardValue() {
        return cardValue;
    }


    /**
     * Getter for probability weighting of card
     * @return Probability weighting of card
     */
    public int getWeighting() {
        return weighting;
    }


    /**
     * Sets probability weight of card depending on if it is preffered or not
     * @param isPreferred Whether the card is preferred by the player or not
     */
    public void setWeighting(boolean isPreferred) {
        if  (isPreferred) {
            this.weighting = this.preferredWeighting;
        } else {
            this.weighting = this.nonPreferredWeighting;
        }
    }


    /**
     * Increases weighting based on preset multiplier
     */
    public void incrementWeighting() {
        this.weighting *= weightingMultiplier;
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
}