package main;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Player implements Runnable {

    private static int nextPlayerID = 1;
    private int playerID;
    private final int NUM_CARDS = 4;
    private Card[] hand = new Card[NUM_CARDS + 1];
    private OutputManager outputManager;
    private volatile Boolean[] winCheckArray;
    private AtomicInteger winningPlayerId;
    private Deck pickUpDeck;
    private Deck discardDeck;

    /**
     * Constructor for Player
     * @param initialHand
     * @param winCheckArray
     * @param pickUpDeck
     * @param discardDeck
     * @throws HandLengthException
     * @throws InvalidCardException
     * @throws IOException
     */
    public Player(int[] initialHand, Boolean[] winCheckArray, AtomicInteger winningPlayerId, Deck pickUpDeck, Deck discardDeck) throws HandLengthException, InvalidCardException, IOException {

        if (initialHand == null) {
            ThreadedLogger.log("NullPointerException in Player() in player " + playerID);
            throw new NullPointerException();
        } else if (initialHand.length != NUM_CARDS) {
            ThreadedLogger.log("HandLengthException in Player() in player " + playerID);
            throw new HandLengthException("Initial hand length must be of length 4");
        }

        this.winCheckArray = winCheckArray;
        this.winningPlayerId = winningPlayerId;

        playerID = Player.nextPlayerID++;
        // Array starts off filled with null values so last element in array is null
        for (int i=0; i < NUM_CARDS; i++) {
            this.hand[i] = new Card(initialHand[i]);
            this.hand[i].setWeighting(initialHand[i] == playerID);
        }

        this.pickUpDeck = pickUpDeck;
        this.discardDeck = discardDeck;

        outputManager = new OutputManager(String.format("player%d_output.txt", playerID), false);
    }

    /**
     * logs player hands to output manager(?)
     * @throws HandLengthException
     * @throws IOException
     */
    private void logCards() throws HandLengthException, IOException {

        StringBuilder message = new StringBuilder();

        message.append(String.format("player%d contents:", playerID));
        message.append(handToString());
        
        this.outputManager.output(message.toString());
    }

    /**
     * converts players hand to string to be printed
     * @return message
     * @throws HandLengthException
     */
    private String handToString() throws HandLengthException {
        int[] outputHand = new int[NUM_CARDS];
        isOneNull();

        outputHand = getNonNullCardValues();
        StringBuilder message = new StringBuilder();
        for (int card : outputHand) {
            message.append(String.format(" %d", card));
        }
        return message.toString();
    }


    /**
     * final log for end of game. closes the output manager
     * @throws IOException
     */
    public void finalPlayerLog(Integer winningPlayerId) throws HandLengthException, IOException {
        // Logs card pick up
        try{
            StringBuilder message = new StringBuilder();

            message.append(String.format("player %d ", winningPlayerId));
            message.append(String.format("has informed player %d ", this.playerID));
            message.append(String.format("that player %d has won", winningPlayerId));

            this.outputManager.output(message.toString());
            this.outputManager.output(String.format("player %d exits", this.playerID));

            message = new StringBuilder();
            message.append(String.format("player%d final hand:", playerID));
            try {
                message.append(handToString());
            } catch (HandLengthException e) {
                ThreadedLogger.log("HandLengthException in finalPlayerLog() in player " + playerID);
                throw new HandLengthException("Hand length must be of length 4");
            }
        } finally {
            this.outputManager.close();
        }
    }


    /**
     * goes through the cards in hand removes nulls e.g. for output manager and logging
     * @return hand without nulls in it
     */
    private int[] getNonNullCardValues() {
        int counter = 0;
        int nonNullCount = 0;

        for (Card card : this.hand) {
            if (card != null) {
                nonNullCount++;
            }
        }        

        int[] outputHand = new int[nonNullCount];
        for (Card card : this.hand) {
            if (card != null) {
                outputHand[counter++] = card.getCardValue();
            }
        }
        return outputHand;
    }

    /**
     * checks if all cards in hand match thus a winning hand
     * @return true if all cards in hand match, false if not
     */
    private boolean winCheck() {
        int[] cardArray = getNonNullCardValues();
    
        if (cardArray.length != NUM_CARDS) {

            return false;
        }
    
        int firstNum = cardArray[0];
        for (int value : cardArray) {
            if (value != firstNum) {
                return false;
            }
        }
        return true;
    }
    

    /**
     * draws card from respective pickup deck for current player, slots it into null space in hand, sets the new card's weighting, logs the draw.
     * @throws HandLengthException
     * @throws DeckLengthException
     * @throws IOException
     */
    public void pickUp() throws HandLengthException, DeckLengthException, IOException {

        int nullIndex  = -1;
        try {
            isOneNull();
        } catch (HandLengthException e) {
            ThreadedLogger.log("HandLengthException in pickUp() in player " + playerID);
            throw new HandLengthException("HandLengthException in pickUp()");
        }
        
        // Find the first null slot in the hand
        for (int i = 0; i < this.hand.length; i++) {
            if (this.hand[i] == null) {
                nullIndex = i;
                break;
            }
        }

        if (nullIndex == -1) {
            ThreadedLogger.log("HandLengthException in pickUp() in player " + playerID);
            throw new HandLengthException(String.format("Player %d hand has no available null slots for a new card.", this.playerID));
        }

        Card newCard = this.pickUpDeck.removeCard();


        // Weighting reset for new card
        newCard.setWeighting(newCard.getCardValue() == this.playerID);
        this.hand[nullIndex] = newCard;
        // Logs card pick up
        StringBuilder message = new StringBuilder();

        message.append(String.format("player %d ", this.playerID));
        message.append(String.format("draws a %d ", newCard.getCardValue()));
        message.append(String.format("from deck %d", this.pickUpDeck.getDeckID()));

        this.outputManager.output(message.toString());
    }

    /**
     * checks if there is one null in hand. needed for space for pickup card.
     * @throws HandLengthException
     */
    private void isOneNull() throws HandLengthException {
        int nonNullCount = 0;
        int nullCount = 0;

        for (Card card : this.hand) {
            if (card == null) {
                nullCount++;
            }
        }
    
        if (nullCount != 1) {
            ThreadedLogger.log("HandLengthException in isOneNull() in player " + playerID);
            throw new HandLengthException(String.format(
                "Player %d hand has %d non-null cards and %d nulls, instead of %d non-null and 1 null.", 
                this.playerID, nonNullCount, nullCount, NUM_CARDS
            ));
        }
    }

    /**
     * finds the card to be discarded from hand. determined by the cards weighting whether preferred or non-preferred.
     * @return index of card to be discarded
     */
    private int getDiscardCardIndex() {
        Random randomObject = new Random();
        int total = 0;

        for (Card card : this.hand) {
            if (card != null && card.getWeighting() > 0) {
                int weighting = card.getWeighting();
                total += weighting;
            }
        }

        if (total <= 0) {
            ThreadedLogger.log("IllegalStateException in getDiscardCardIndex() in player " + playerID);
            throw new IllegalStateException("Hand contains no valid cards.");
        }

        // Adds one as the lowest value should be 1
        int randomNum = randomObject.nextInt(total) + 1;

        int index = -1;
        while (randomNum > 0) {
            if (this.hand[++index] != null && this.hand[index].getWeighting() > 0) {
                randomNum -= this.hand[index].getWeighting();
            }
        }
        return index;
    }


    /**
     * discards card to discard deck. increments weighting of all remaining cards. logs discard.
     * @return discard card
     * @throws DeckLengthException
     * @throws IOException
     */
    public Card discard() throws DeckLengthException, IOException {

        int index = getDiscardCardIndex();
        Card discardCard = this.hand[index];

        this.hand[index] = null;
        try{
            this.discardDeck.addCard(discardCard);
        } catch (IOException e) {
            ThreadedLogger.log("IOException in discard() in player " + playerID);
            throw e;
        }

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
        try {
            this.outputManager.output(message.toString());
        } catch (IOException e) {
            ThreadedLogger.log("IOException in discard() in player " + playerID);
            throw e;
        }
  
        return discardCard;
    }

    public void run() {
        // Publishes results
        this.winCheckArray[playerID-1] = (Boolean) winCheck();
        // Waits for other results
        try {
            while (!Thread.interrupted()) {
                TimeUnit.MILLISECONDS.sleep(1);
                }
        } catch (InterruptedException e) {}
        
        // Checks for other results
        while (this.winningPlayerId.get() == 0) {
            try{
                this.pickUp();
                this.discard();
                this.logCards();
            } catch (HandLengthException e) {
                ThreadedLogger.log("HandLengthException in player " + playerID);
            } catch (DeckLengthException e) { 
                ThreadedLogger.log("DeckLengthException in player " + playerID);
            } catch (IOException e) {
                ThreadedLogger.log("IOException in player " + playerID);
            }

            // Publishes results
            this.winCheckArray[playerID-1] = (Boolean) winCheck();
            // Wait for interrupt
            try {
                while (!Thread.interrupted()) {
                    TimeUnit.MILLISECONDS.sleep(1);
                }
            } catch (InterruptedException e) {}
        }
    }

    public static void main(String[] args) throws HandLengthException, InvalidCardException, IOException {}
}
