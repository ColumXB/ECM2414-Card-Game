package main;
public class InvalidCardException extends Exception {
    /**
     * Throws an exception for when a card has an invalid value
     * @param message text shown when with the exception raised
     */
    public InvalidCardException(String message) {
        super(message);
    }
}
