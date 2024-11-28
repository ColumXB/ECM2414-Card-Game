package main;

public class HandLengthException extends Exception {


    /**
     * Throws an exception for when a hand is the incorrect length
     * @param message text shown when with the exception raised
     */
    public HandLengthException(String message) {
        super(message);
    }
}
