package main;

public class InvalidFileException extends Exception {
    
    // Constructor that takes a message
    public InvalidFileException(String message) {
        super(message);
    }
}
