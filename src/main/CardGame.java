package main;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class CardGame {

    public static AtomicInteger winningPlayerId;

    public static class Utility{
        /**
         * checks if the win check array is full
         * @return true if win check array is full, false if not
         */
        public static boolean isWinCheckArrayFull(Boolean[] winCheckArray) {
            for (Boolean value : winCheckArray) {
                if (value == null) {
                    return false;
                }
            }
            return true;
        }

        /**
         * 
         * @param winCheckArray
         * @return index of the first true value found, if none found then '-1' returned
         */
        public static int findWinningIndex(Boolean[] winCheckArray) {
            int counter = 0;
            try{
                for (Boolean hasWon: winCheckArray) {
                    if (hasWon) {
                        return counter;
                    }
                    counter++;
                }
            } catch (NullPointerException e) {
                throw new NullPointerException("Null in winCheckArray");
            }
            return -1;
        }

        public static void printArray(Boolean[] array) {
            StringBuilder output = new StringBuilder();
            output.append("[");
            for (Boolean element : array) {
                output.append(" ");
                if (element == null) {
                    output.append("null");
                } else {
                    output.append(element.toString());
                } 
            }
            output.append(" ]");
            System.out.println(output.toString());
        }

        public static void clearArray(Boolean[] array) {
            for (int i = 0; i<array.length; i++) {
                array[i] = null;
            }
        }
    }

    public static void main(String[] args) throws Exception {
        PackHandler oops = new PackHandler();
        oops.inputs();
        oops.packReader();
        int[][][] gameArray = oops.getGameArray();
        int numPlayers = oops.getNumPlayers();
        Deck[] deckArray = new Deck[numPlayers];
        Player[] playerArray = new Player[numPlayers];
        Thread[] threadArray = new Thread[numPlayers];
        winningPlayerId = new AtomicInteger();
        winningPlayerId.set(0);
        Boolean[] winCheckArray = new Boolean[numPlayers];

        //TODO remove
        StringBuilder penis;
    
        //deck creation
        try {
            for (int i = 0; i<numPlayers; i++) {
                deckArray[i] = new Deck(gameArray[1][i]);
            }
        } catch (Exception e) {
            throw new Exception("oh no!");
        }

        //player and thread creation
        int discardDeckIndex;
        try {
            for (int i = 0; i<numPlayers; i++) {
                if (i==numPlayers-1) {
                    discardDeckIndex = 0;
                } else {
                    discardDeckIndex = i+1;
                }
                playerArray[i] = new Player(gameArray[0][i], winCheckArray, winningPlayerId, deckArray[i], deckArray[discardDeckIndex]);
                threadArray[i] = new Thread(playerArray[i]);
                System.out.println(i+ " " + discardDeckIndex);
            }
        } catch (Exception e) {
            throw new Exception("oh no!");
        }

        //starting threads
        for (Thread thread : threadArray) {
            thread.start();
        }

        TimeUnit.SECONDS.sleep(5);

        //game loop
        try {
            // Check for all results
            while (winningPlayerId.get() == 0) {

                // Wait for array to fill with results
                Utility.printArray(winCheckArray);
                while (!Utility.isWinCheckArrayFull(winCheckArray)) {
                    TimeUnit.MILLISECONDS.sleep(1);
                }
                Utility.printArray(winCheckArray);

                // Pull first winning results
                winningPlayerId.set(Utility.findWinningIndex(winCheckArray));
                winningPlayerId.getAndIncrement();

                // Clear the array
                Utility.clearArray(winCheckArray);

                // Start players again
                for (Thread thread : threadArray) {
                    thread.interrupt();
                }
                
            }
        } finally {
            for (int i = 0; i<numPlayers; i++) {
                System.out.println("final winningPlayerId: " + winningPlayerId);
                deckArray[i].finalDeckLog();
                playerArray[i].finalPlayerLog(winningPlayerId.get());
            }
        }
    }
}
