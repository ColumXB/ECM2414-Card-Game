package main;

public class InvalidPlayersException extends Exception {
    /**
     * Throws an exception for when a card has an invalid value
     * @param message text shown when with the exception raised
     */
    public InvalidPlayersException(String message) {
        super(message);
    }
}
