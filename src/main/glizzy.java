package main;

public class glizzy {
    public static void main(String[] args) throws Exception{
        int[] initialHand = {1, 2, 3, 4}; 
        Boolean[] winCheckArray = {null, null}; 
        Deck pickUpDeck = new Deck(initialHand); 
        Deck discardDeck = new Deck(initialHand);
        

        Thread t1 = new Thread(new Player(initialHand, winCheckArray, pickUpDeck, discardDeck));
        Thread t2 = new Thread(new Player(initialHand, winCheckArray, discardDeck, pickUpDeck));
        t1.start();
        t2.start();

        while (true) {
            if (Player.flag) {
                Player.flag = false;
                t1.interrupt();
                t2.interrupt();
            }
        }
    }
}
