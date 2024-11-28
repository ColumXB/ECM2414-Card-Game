package main;

import java.io.IOException;

public class Player extends Thread{

    private static int nextPlayerID = 1;
    private int playerID;
    private final int NUM_CARDS = 4;
    private Card[] hand = new Card[NUM_CARDS + 1];
    OutputManager outputManager;


    /**
     * 
     * @param initialHand
     * @throws HandLengthException
     * @throws InvalidCardException
     * @throws IOException
     */
    public Player(int[] initialHand) throws HandLengthException, InvalidCardException, IOException {
        if (initialHand == null) {
            throw new NullPointerException();
        } else if (initialHand.length != NUM_CARDS) {
            throw new HandLengthException("Initial hand length must be of length 4");
        }

        // Array starts off filled with null values so last element in array is null
        for (int i=0; i < NUM_CARDS; i++) {
            this.hand[i] = new Card(initialHand[i]);
        }

        playerID = Player.nextPlayerID++;
        outputManager = new OutputManager(String.format("player%d_output.txt", playerID));
    }


    public void logCards() throws HandLengthException, IOException {

        int counter = 0;
        int[] outputHand = new int[NUM_CARDS];

        // Error catching logic
        isOneNull();

        outputHand = getNonNullCardValues();

        // Message logic
        StringBuilder message = new StringBuilder();

        message.append(String.format("player%d contents:", playerID));
        for (int card : outputHand) {
            message.append(String.format(" %d", card));
        }
        this.outputManager.output(message.toString());
    }


    public void finalPlayerLog() throws IOException {
        this.outputManager.close();
    }


    private int[] getNonNullCardValues() {
        int counter = 0;
        int[] outputHand = new int[NUM_CARDS]; //TODO add catch for when there are 2 or more nulls
        for (Card card : this.hand) {
            if (card != null) {
                outputHand[counter] = card.getCardValue();
                counter++;
            }
        }
        return outputHand;

    }


    private boolean winCheck() {
        int counter = 1;
        int[] cardArray = getNonNullCardValues();

        // Counts how many card values are equivalent to the first
        int firstNum = cardArray[0];
        for (int i=1; i<NUM_CARDS; i++) {
            if (firstNum == cardArray[i]) {
                counter++;
            }
        }

        return counter == NUM_CARDS;   
    }


    public void PickUp(Deck pickupDeck) throws HandLengthException, DeckLengthException, IOException {
        int counter = 0;
        isOneNull();
        
        while (this.hand[counter] != null) {
            counter++;
        }

        Card newCard = pickupDeck.removeCard();

        newCard.setWeighting(newCard.getCardValue() == this.playerID);
        this.hand[counter] = newCard;

        // Logs card pick up
        StringBuilder message = new StringBuilder();

        message.append(String.format("player %d ", this.playerID));
        message.append(String.format("draws a %d ", newCard.getCardValue()));
        message.append(String.format("from deck %d", pickupDeck.getDeckID()));

        this.outputManager.output(message.toString());
    }


    private void isOneNull() throws HandLengthException {
        int counter = 0;
        for (Card card : this.hand) {
            if (card != null) {
                counter++;
            }
        }
        if (counter != NUM_CARDS) {
            throw new HandLengthException("Cannot log hand that doesn't have 4 cards");
        }
    }


    public void run() {}

    public static void main(String[] args) throws HandLengthException, InvalidCardException, IOException {

        int[] rizzler = {1, 2, 3, 4};
        Player oops = new Player(rizzler);
        oops.logCards();
        oops.finalPlayerLog();
    }
}
