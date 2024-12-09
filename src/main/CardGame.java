package main;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class CardGame {
    public AtomicInteger winningPlayerId;

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
                ThreadedLogger.log("NullPointerException, Null in winCheckArray");
                throw new NullPointerException();
            }
            return -1;
        }

        public static void clearArray(Boolean[] array) {
            for (int i = 0; i<array.length; i++) {
                array[i] = null;
            }
        }
    }

    public static void main(String[] args) throws HandLengthException, InvalidCardException, IOException, DeckLengthException, InvalidCardException {

        Thread logger = new Thread(new ThreadedLogger());
        logger.start();

        CardGame game = new CardGame();
        PackHandler oops = new PackHandler();
        oops.inputs();
        oops.packReader();
        int[][][] gameArray = oops.packSplitter();
        int numPlayers = oops.getNumPlayers();
        Deck[] deckArray = new Deck[numPlayers];
        Player[] playerArray = new Player[numPlayers];
        Thread[] threadArray = new Thread[numPlayers];
        game.winningPlayerId = new AtomicInteger();
        game.winningPlayerId.set(0);
        Boolean[] winCheckArray = new Boolean[numPlayers];

        
    
        //deck creation
        try {
            for (int i = 0; i<numPlayers; i++) {
                deckArray[i] = new Deck(gameArray[1][i]);
            }
        } catch (DeckLengthException e) {
            ThreadedLogger.log("DeckLengthException in deck creation in CardGame");
            throw e;
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
                playerArray[i] = new Player(gameArray[0][i], winCheckArray, game.winningPlayerId, deckArray[i], deckArray[discardDeckIndex]);
                threadArray[i] = new Thread(playerArray[i]);
                System.out.println(i+ " " + discardDeckIndex);
            }
        } catch (HandLengthException e) {
            ThreadedLogger.log("HandLengthException in player and thread creation in CardGame");
            throw e;
        } catch (InvalidCardException e) {
            ThreadedLogger.log("InvalidCardException in player and thread creation in CardGame");
            throw e;
        } catch (IOException e) {
            ThreadedLogger.log("IOException in player and thread creation in CardGame");
            throw e;
        }

        //starting threads
        for (Thread thread : threadArray) {
            thread.start();
        }

        //game loop
        try {
            // Check for all results
            while (game.winningPlayerId.get() == 0) {

                // Wait for array to fill with results
                while (!Utility.isWinCheckArrayFull(winCheckArray)) {
                    try {
                        TimeUnit.MILLISECONDS.sleep(1);
                    } catch (InterruptedException e) {}
                }

                // Pull first winning results
                game.winningPlayerId.set(Utility.findWinningIndex(winCheckArray));
                game.winningPlayerId.getAndIncrement();

                // Clear the array
                Utility.clearArray(winCheckArray);

                // Start players again
                for (Thread thread : threadArray) {
                    thread.interrupt();
                }
                
            }
        } finally {
            for (int i = 0; i<numPlayers; i++) {
                deckArray[i].finalDeckLog();
                playerArray[i].finalPlayerLog(game.winningPlayerId.get());
                ThreadedLogger.shutDown();
            }
        }
    }
}
