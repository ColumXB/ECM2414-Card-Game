package main;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Random;

public class Player implements Runnable {

    private static int nextPlayerID = 1;
    private int playerID;
    private final int NUM_CARDS = 4;
    private Card[] hand = new Card[NUM_CARDS + 1];
    private OutputManager outputManager;
    private volatile Boolean[] winCheckArray;
    private volatile Integer winningPlayerId;
    private Deck pickUpDeck;
    private Deck discardDeck;
    public static volatile boolean flag = false;
    //TODO remove
    DateTimeFormatter myDateObject = DateTimeFormatter.ofPattern("HH:mm:ss");

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
    public Player(int[] initialHand, Boolean[] winCheckArray, Integer winningPlayerId, Deck pickUpDeck, Deck discardDeck) throws HandLengthException, InvalidCardException, IOException {

        if (initialHand == null) {
            throw new NullPointerException();
        } else if (initialHand.length != NUM_CARDS) {
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


        outputManager = new OutputManager(String.format("player%d_output.txt", playerID));
    }

    /**
     * logs player hands to output manager(?)
     * @throws HandLengthException
     * @throws IOException
     */
    public void logCards() throws HandLengthException, IOException {

        // Message logic
        StringBuilder message = new StringBuilder();

        message.append(String.format("player%d contents:", playerID));
        message.append(handtoString());
        
        this.outputManager.output(message.toString());
    }

    private String handtoString() throws HandLengthException {
        int[] outputHand = new int[NUM_CARDS];
        // Error catching logic
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
    public void finalPlayerLog(Integer winningPlayerId) throws IOException {
        // Logs card pick up
        StringBuilder message = new StringBuilder();

        message.append(String.format("player %d ", winningPlayerId));
        message.append(String.format("has informed player %d ", this.playerID));
        message.append(String.format("that player %d has won", winningPlayerId));

        this.outputManager.output(message.toString());


        this.outputManager.output(String.format("player %d exits", this.playerID));

        message = new StringBuilder();

        message.append(String.format("player%d final hand:", playerID));
        try {
            message.append(handtoString());
        } catch (HandLengthException e) {
            //TODO log Handlengthexception
        }

        this.outputManager.close();
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
        } catch (HandLengthException e) {//TODO log
            
            throw e;
        }
        
        // Find the first null slot in the hand
        for (int i = 0; i < this.hand.length; i++) {
            if (this.hand[i] == null) {
                nullIndex = i;
                break;
            }
        }

        if (nullIndex == -1) {
            throw new HandLengthException(String.format("Player %d hand has no available null slots for a new card.", this.playerID));
        }

        Card newCard = this.pickUpDeck.removeCard(); //TODO DECK LENGTH EXCEPTION BEING THROWN HERE


        // Weighting reset for new card
        newCard.setWeighting(newCard.getCardValue() == this.playerID);
        this.hand[nullIndex] = newCard;
        //TODO remove
        this.outputManager.output(LocalDateTime.now().format(this.myDateObject));
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

            if (card != null) {
                nonNullCount++;
            } else {
                nullCount++;
            }
        }
    
        if (nonNullCount != NUM_CARDS || nullCount != 1) {
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
                int weighting = card.getWeighting(); // Get the card's weighting
                
                total += weighting;
            }
        }

        if (total <= 0) {
            throw new IllegalStateException("Hand contains no valid cards."); //TODO wincheck should happen before this anyway
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
     * discard card to discard deck. increments weighting of all remaining cards. logs discard.
     * @return discard card
     * @throws DeckLengthException
     * @throws IOException
     */
    public Card discard() throws DeckLengthException, IOException {//TODO WINCHECK BEFORE HERE

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
        //TODO remove 
        this.outputManager.output(LocalDateTime.now().format(this.myDateObject));
        // Logs card discard
        StringBuilder message = new StringBuilder();

        message.append(String.format("player %d ", this.playerID));
        message.append(String.format("discards a %d ", discardCard.getCardValue()));
        message.append(String.format("to deck %d", this.discardDeck.getDeckID()));

        this.outputManager.output(message.toString());
  
        return discardCard;
    }

    public void run() {
        this.winCheckArray[playerID-1] = (Boolean) winCheck();
        try {
            //Thread.sleep(1000);
            while (!Thread.interrupted()) {}
        } catch (Exception e) {
            System.out.println("Interrupted");
        }
        while (this.winningPlayerId == 0) {
            System.out.println("bad");
            try{
                this.pickUp();
                this.discard();
                this.logCards();
            } catch (HandLengthException e) {
                //TODO log handlengthexception
                //System.out.println("Handlengthexception"); //TODO here
            } catch (DeckLengthException e) { 
                //TODO log Decklengthexception
                System.out.println("Decklengthexception");
            } catch (IOException e) {
                //TODO log IOexception
                System.out.println("IOexception");
            }
            this.winCheckArray[playerID-1] = (Boolean) winCheck();
            try {
                System.out.println("eep");
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                System.out.println("poop");
            }
        }
    }

    public static void main(String[] args) throws HandLengthException, InvalidCardException, IOException {
    }
} //TODO Goals: Fix the decklengthexception (both players drawing from same deck), logging, syncronization/one thread being much further ahead than the other
