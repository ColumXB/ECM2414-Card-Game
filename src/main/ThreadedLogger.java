package main;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

public class ThreadedLogger implements Runnable {
    
    private static Queue<String> messageStream;
    private static OutputManager outputManager;
    private static DateTimeFormatter timeFormatter;

    static {
        messageStream = new LinkedList<String>();
        timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss ");
        try {
            outputManager = new OutputManager("card_game.log", true);
        } catch (IOException e) {
            System.out.println(e);
            throw new RuntimeException("Output Manager failed to instatiate");
        }
        
    }

    private boolean isRunning = false;
    private static int threadCounter = 0;


    /**
     * Constructor to allow for thread to run
     */
    public ThreadedLogger() {}


    /**
     * Times stamps and sends message to the message stream to be logged ASAP
     * @param message Message to be logged
     */
    public static synchronized void log(String message) {

        if (message == null) {
            throw new NullPointerException("Cannot log null message");
        }

        
        StringBuilder timeMessage = new StringBuilder();

        timeMessage.append(LocalDateTime.now().format(timeFormatter));
        timeMessage.append(message);

        messageStream.add(timeMessage.toString());
    }


    /**
     * Sends a close request that is fulfilled once all messages in queue are logged
     */
    public void shutDown() {
        this.isRunning = false;
    }


    public void run() {
        this.isRunning = true;

        // Allows only one thread to run at a time
        if (++threadCounter > 1) {
            isRunning = false;
            System.out.println("Only one thread can run at a time for ThreadedLogger");
        }

        // Runs until flag is changed via shutDown method
        while (this.isRunning) {

            // Small sleep to let while loop run properly
            try {
                TimeUnit.MILLISECONDS.sleep(1);
            } catch (InterruptedException e) {}

            // Constantly tries to clear queue of messages by sending them to the file
            while (!messageStream.isEmpty()) {
                try {
                    outputManager.output(messageStream.poll());
                } catch (IOException e) {
                    System.out.println("Error in ThreadedLogger thread");
                    System.out.println(e);
                }
            }
        }

        // Closes the logger
        try {

            if (threadCounter > 1) {
                threadCounter--;

            } else {
                outputManager.close();
            }
            
        } catch (IOException e) {
            System.out.println(e);
            System.out.println("Logger has failed to close successfully");
        }
    }
}
