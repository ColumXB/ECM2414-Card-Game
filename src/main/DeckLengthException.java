package main;

public class DeckLengthException extends Exception {


    /**
     * Throws an exception for when a deck is the incorrect length
     * @param message text shown when with the exception raised
     */
    public DeckLengthException(String message) {
        super(message);
    }
}
