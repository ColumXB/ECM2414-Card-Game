package main;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

public class Deck {
    
    private Queue<Card> cardDeck;
    private OutputManager outputManager;
    private static int nextDeckID = 1;
    private int deckID;
    private final int NUM_CARDS = 4;
    private boolean isClosed = false;


    /**
     * Constructor for Deck
     * Creates a unique ID for the deck and initialises the logger for the deck
     * @param initialDeck Array of {@link NUM_CARDS} Integers for each of the 4 Cards
     * @throws DeckLengthException The array of cards given does not contain 4 cards
     * @throws IOException
     * @throws InvalidCardException The cards numbers given are not valid
     */
    public Deck(int[] initialDeck) throws DeckLengthException, IOException, InvalidCardException {
        if (initialDeck == null) {
            throw new NullPointerException();
        } else if (initialDeck.length != NUM_CARDS) {
            throw new DeckLengthException("Initial deck length must be of length 4");
        }

        // Setting up object
        this.cardDeck = new LinkedList<Card>();
        this.deckID = Deck.nextDeckID++;
        this.outputManager = new OutputManager(String.format("deck%d_output.txt", deckID));

        // Filling object with Card objects
        for (int initialCard: initialDeck) {
            this.cardDeck.add(new Card(initialCard));
        }

    }


    /**
     * Gets the contents of the deck
     * @return Array of the numbers of the cards in the deck
     * @throws DeckLengthException Deck length must be 4 to get the contents
     */
    private int[] getContents() throws DeckLengthException {

        if (this.cardDeck.size() != NUM_CARDS) {
            throw new DeckLengthException("Deck length must be 4 when collecting");
        }

        Card[] cardDeckArray = new Card[NUM_CARDS];
        this.cardDeck.toArray(cardDeckArray);
        int[] intDeckArray = new int[NUM_CARDS];

        for (int i = 0; i < NUM_CARDS; i++) {
            intDeckArray[i] = cardDeckArray[i].getCardValue();
        }
        return intDeckArray;
    }


    /**
     * Logs the deck given to the deck's unique file under its own unique name
     * @param cardDeckArray Deck to be logged
     * @throws IOException
     */
    private void logDeck() throws IOException, DeckLengthException {

        int[] cardDeckArray = this.getContents();

        StringBuilder message = new StringBuilder();

        message.append(String.format("deck%d contents:", deckID));
        for (int card : cardDeckArray) {
            message.append(String.format(" %d", card));
        }

        this.outputManager.output(message.toString());
    }


    /**
     * Adds card to the bottom of the deck
     * @param newCard Card object to be added to the deck
     * @throws DeckLengthException Cards cannot be added to a deck that 5 or more cards
     * @throws IOException
     */
    public void addCard(Card newCard) throws DeckLengthException, IOException {

        if (isClosed) {
            throw new IOException("Cannot add to closed deck");
        } else if (this.cardDeck.size() >= NUM_CARDS + 1) {
            throw new DeckLengthException("Deck contains too many cards (5 or more)");
        }

        this.cardDeck.add(newCard);
    }


    /**
     * Removes and returns the card from the top of the deck
     * @return Card object removed from the deck
     * @throws DeckLengthException Card cannot be removed from a deck with 3 or less cards
     * @throws IOException
     */
    public Card removeCard() throws DeckLengthException, IOException {

        if (isClosed) {
            throw new IOException("Cannot remove from closed deck");
        } else if (this.cardDeck.size() <= NUM_CARDS - 1) {
            throw new DeckLengthException("Deck contains too little cards (3 or less)");
        }

        Card newCard = this.cardDeck.poll();

        return newCard;
    }


    /**
     * Closes the logger used internally
     * @throws IOException
     */
    public void finalDeckLog() throws IOException, DeckLengthException{
        if (isClosed) {
            throw new IOException("Cannot close closed deck");
        }
        try {
            this.logDeck();
        } finally {
            this.outputManager.close();
            this.isClosed = true;
        }
    }


    /**
     * Getter for deck's ID
     * @return The ID of the deck
     */
    public int getDeckID() {
        return deckID;
    }
}
