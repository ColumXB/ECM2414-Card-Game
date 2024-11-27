package main;

public class InvalidCardValueException extends Exception {
    
    // Constructor that takes a message
    public InvalidCardValueException(String message) {
        super(message);
    }

    public InvalidCardValueException(String message, Throwable cause) {
        super(message, cause);
    }
}
