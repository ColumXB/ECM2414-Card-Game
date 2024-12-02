package main;

import java.io.IOException;
import java.util.Random;

public class Player implements Runnable {

    private static int nextPlayerID = 1;
    private int playerID;
    private final int NUM_CARDS = 4;
    private Card[] hand = new Card[NUM_CARDS + 1];
    private OutputManager outputManager;
    private volatile Boolean[] winCheckArray;
    private Deck pickUpDeck;
    private Deck discardDeck;
    public static volatile boolean flag = false;


    /**
     * 
     * @param initialHand
     * @throws HandLengthException
     * @throws InvalidCardException
     * @throws IOException
     */
    public Player(int[] initialHand, Boolean[] winCheckArray, Deck pickUpDeck, Deck discardDeck) throws HandLengthException, InvalidCardException, IOException {
        if (initialHand == null) {
            throw new NullPointerException();
        } else if (initialHand.length != NUM_CARDS) {
            throw new HandLengthException("Initial hand length must be of length 4");
        }

        this.winCheckArray = winCheckArray;

        // Array starts off filled with null values so last element in array is null
        for (int i=0; i < NUM_CARDS; i++) {
            this.hand[i] = new Card(initialHand[i]);
        }

        this.pickUpDeck = pickUpDeck;
        this.discardDeck = discardDeck;

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


    public void pickUp() throws HandLengthException, DeckLengthException, IOException {
        int counter = 0;
        isOneNull();
        
        while (this.hand[counter] != null) {
            counter++;
        }

        Card newCard = this.pickUpDeck.removeCard();

        // Weighting reset for new card
        newCard.setWeighting(newCard.getCardValue() == this.playerID);
        this.hand[counter] = newCard;

        // Logs card pick up
        StringBuilder message = new StringBuilder();

        message.append(String.format("player %d ", this.playerID));
        message.append(String.format("draws a %d ", newCard.getCardValue()));
        message.append(String.format("from deck %d", this.pickUpDeck.getDeckID()));

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

    
    private int getDiscardCardIndex() {

        Random randomObject = new Random();

        int total = 0;
        for (Card card : this.hand) {
            if (card != null) {
                total += card.getWeighting();
            }
        }

        // Adds one as the lowest value should be 1
        int randomNum = randomObject.nextInt(total) + 1;

        int index = -1;
        while (randomNum > 0) {
            if (this.hand[++index] != null) {
                randomNum -= this.hand[index].getWeighting();
            }
        }

        return index;
    }


    public Card discard() throws DeckLengthException, IOException {
        int index = getDiscardCardIndex();

        // Takes card 
        Card discardCard = this.hand[index];
        this.hand[index] = null;
        this.discardDeck.addCard(discardCard);

        // Increments weighting for all leftover cards
        for (Card card : this.hand) {
            if (card != null) {
                card.incrementWeighting();
            }
        }

        // Logs card discard
        StringBuilder message = new StringBuilder();

        message.append(String.format("player %d ", this.playerID));
        message.append(String.format("discards a %d ", discardCard.getCardValue()));
        message.append(String.format("to deck %d", this.discardDeck.getDeckID()));

        this.outputManager.output(message.toString());

        return discardCard;
    }


    public boolean isWinCheckArrayFull() {
        for (Boolean value : this.winCheckArray) {
            if (value == null) {
                return false;
            }
        }
        return true;
    }


    public void clearFalses() {

        for (int i = 0; i < this.winCheckArray.length; i++) {
            if (this.winCheckArray[i] == false) {
                this.winCheckArray[i] = null;
            }
        }
    }

    //private int get


    public void run() {

        while (true) {
            winCheckArray[playerID-1] = (Boolean) winCheck();
            System.out.println(String.format("winCheck %d", this.playerID));
            if (!isWinCheckArrayFull()) {
                try {
                    System.out.println(String.format("eepy %d", this.playerID));
                    Thread.sleep(99999); //TODO Change
                } catch (InterruptedException e) {
                    System.out.println(String.format("caught %d", this.playerID));

                }
            } else {
                System.out.println(String.format("true flag %d", this.playerID));
                flag = true;
            }
            System.out.println(String.format("pick disc %d", this.playerID));
            try {
                this.pickUp();
                this.discard();
            } catch (Exception e) {
                System.out.println("poo");
                System.out.println(e.getMessage());
            }
            System.out.println(String.format("end %d", this.playerID));
        }

    }

    //TODO remove
    public static void main(String[] args) throws HandLengthException, InvalidCardException, IOException {

        int[] rizzler = {1, 2, 3, 4};
        //Player oops = new Player();
        //oops.logCards();
        //oops.finalPlayerLog();
    }
}
