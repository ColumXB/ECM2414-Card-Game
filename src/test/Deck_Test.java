package test;

import main.Card;
import main.Deck;
import main.DeckLengthException;
import main.InvalidCardException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Queue;
import java.util.Scanner;

import org.junit.Test;
import org.junit.jupiter.api.DisplayName;


public class Deck_Test {

    private int[] testDeck = {1, 2, 3, 4};
    

    /**
     * Tests the constructor runs with no error
     */
    @DisplayName("Basic Constructor Test")
    @Test
    public void constructorTest() {
        int[] deck1 = this.testDeck;
        assertDoesNotThrow(() -> new Deck(deck1).finalDeckLog());
    }


    /**
     * Tests that decks with invalid lengths cannot be made
     */
    @DisplayName("Invalid Deck Length Test")
    @Test
    public void deckLengthTest() {
        int[] deck1 = {1, 2, 3};
        int[] deck2 = {1, 2, 3, 4, 5};
        assertThrows(DeckLengthException.class, () -> new Deck(deck1).finalDeckLog());
        assertThrows(DeckLengthException.class, () -> new Deck(deck2).finalDeckLog());
    }


    /**
     * Tests that a deck with invalid card numbers cannot be made
     */
    @DisplayName("Invalid Card Test")
    @Test
    public void cardTest() {
        int[] deck1 = {0, 0, 0, 0};
        assertThrows(InvalidCardException.class, () -> new Deck(deck1).finalDeckLog());
    }


    /**
     * Tests that a deck cannot be made with a null
     */
    @DisplayName("Null Deck Test")
    @Test
    public void nullDeckTest() {
        assertThrows(NullPointerException.class, () -> new Deck(null));
    }


    /**
     * Tests that the deck IDs increase by 1 for each new ID
     * @throws Exception
     */
    @DisplayName("Deck ID Test")
    @Test
    public void deckIDTest() throws Exception {
        Deck deck1 = new Deck(this.testDeck);
        Deck deck2 = new Deck(this.testDeck);

        Field deckIDField = Deck.class.getDeclaredField("deckID");
        deckIDField.setAccessible(true);

        int deck1ID = deckIDField.getInt(deck1);
        int deck2ID = deckIDField.getInt(deck2);

        assertEquals(deck1ID+1, deck2ID);

        deck1.finalDeckLog();
        deck2.finalDeckLog();
    }


    /**
     * Tests that an output file is created for a deck
     * @throws Exception
     */
    @DisplayName("Basic Log File Test")
    @Test
    public void logFileTest() throws Exception {

        // Creates a deck so we can find the current ID number we are working with
        Deck deck1 = new Deck(this.testDeck);
        int currentID = deck1.getDeckID();
        deck1.finalDeckLog();

        // Deletes the output file that the next deck will generate if it exists
        File logFile = new File(String.format("deck%d_output.txt", ++currentID));
        logFile.delete();

        Deck deck2 = new Deck(this.testDeck);
        deck2.finalDeckLog();

        assert(logFile.exists());
    }


    /**
     * Tests that a log is made to the output file upon closure of the deck
     * @throws Exception
     */
    @DisplayName("Closure Log Test")
    @Test
    public void closureLogTest() throws Exception {

        Deck deck1 = new Deck(this.testDeck);
        int deck1ID = deck1.getDeckID();
        deck1.finalDeckLog();

        File file1 = new File(String.format("deck%d_output.txt", deck1ID));
        Scanner reader1 = new Scanner(file1);
        assertEquals(String.format("deck%d contents: 1 2 3 4", deck1ID), reader1.nextLine());
        reader1.close();
    }


    /**
     * Tests that the private getContents method obtains the correct data
     * @throws Exception
     */
    @DisplayName("Deck Getter Test")
    @Test
    public void deckGetterTest() throws Exception {
        Deck deck1 = new Deck(this.testDeck);
        Method indexOfMethod = Deck.class.getDeclaredMethod("getContents");
        indexOfMethod.setAccessible(true);

        int[] actualDeck = (int[]) indexOfMethod.invoke(deck1);

        assert(Arrays.equals(this.testDeck, actualDeck));

        deck1.finalDeckLog();
    }


    /**
     * Tests that the internal private logger works as expected
     * @throws Exception
     */
    @DisplayName("Deck Logger Test")
    @Test
    public void deckLoggerTest() throws Exception {

        // Runs the internal logger for the deck
        Deck deck1 = new Deck(this.testDeck);
        int deck1ID = deck1.getDeckID();
        Method indexOfMethod = Deck.class.getDeclaredMethod("logDeck");
        indexOfMethod.setAccessible(true);
        indexOfMethod.invoke(deck1);
        // This also logs but is required other the scanner cannot access the file
        deck1.finalDeckLog();

        // Checks that there has been a log and that the closure log also went through
        File file1 = new File(String.format("deck%d_output.txt", deck1ID));
        Scanner reader1 = new Scanner(file1);
        assertEquals(String.format("deck%d contents: 1 2 3 4", deck1ID), reader1.nextLine());
        assertDoesNotThrow(() -> reader1.nextLine());
        reader1.close();
    }


    /**
     * Tests that the private getContents method will raise an error when the deck size isn't 5 and is run
     * @throws Exception
     */
    @DisplayName("Deck Getter Size Test")
    @Test
    public void deckGetterSizeTest() throws Exception {

        Deck deck1 = new Deck(this.testDeck);
        Deck deck2 = new Deck(this.testDeck);

        Method indexOfMethod = Deck.class.getDeclaredMethod("getContents");
        indexOfMethod.setAccessible(true);

        // Test for an oversized deck
        deck1.addCard(new Card(5));
        assertThrows(InvocationTargetException.class, () -> indexOfMethod.invoke(deck1));

        // Test for an undersized deck
        deck2.removeCard();
        assertThrows(InvocationTargetException.class, () -> indexOfMethod.invoke(deck2));

        // Safely closing decks without error
        deck1.removeCard();
        deck2.addCard(new Card(1));
        deck1.finalDeckLog();
        deck2.finalDeckLog();
    }


    /**
     * Tests the addCard method works as expected
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    @DisplayName("Add Test")
    @Test
    public void addTest() throws Exception {
        int testCardValue = 5;

        Deck deck1 = new Deck(this.testDeck);
        deck1.addCard(new Card(testCardValue));
        
        Field queueField = Deck.class.getDeclaredField("cardDeck");
        queueField.setAccessible(true);
        Queue<Card> queue1 = (Queue<Card>) queueField.get(deck1);

        // Tests the size of the queue is changed
        assertEquals(5, queue1.size());
        
        // Tests the actual value has been added in the correct spot
        Card[] array1 = new Card[5];
        queue1.toArray(array1);
        assertEquals(array1[4].getCardValue(), testCardValue);

        deck1.removeCard();
        deck1.finalDeckLog();
    }


    /**
     * Tests the removeCard method works as expected
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    @DisplayName("Remove Test")
    @Test
    public void removeTest() throws Exception {
        Deck deck1 = new Deck(this.testDeck);
        Card card1 = deck1.removeCard();
        assertEquals(card1.getCardValue(), this.testDeck[0]);

        Field queueField = Deck.class.getDeclaredField("cardDeck");
        queueField.setAccessible(true);
        Queue<Card> queue1 = (Queue<Card>) queueField.get(deck1);
        assertEquals(queue1.size(), 3);

        deck1.addCard(new Card(1));
        deck1.finalDeckLog();
    }


    /**
     * Tests that an error is raised when a closure of deck is attempted twice
     * @throws Exception
     */
    @DisplayName("Double Closure Test")
    @Test
    public void doubleClosureTest() throws Exception {
        Deck deck1 = new Deck(this.testDeck);
        deck1.finalDeckLog();

        // Checks an error is raised upon second closure
        Exception exception = assertThrows(IOException.class, () -> deck1.finalDeckLog());
        // Tests the correct error message is done
        String expectedMessage = "Cannot close closed deck";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }


    /**
     * Tests that an error is raised when the addCard method is run on a closed deck
     * @throws Exception
     */
    @DisplayName("Closed Add Test")
    @Test
    public void closedAddTest() throws Exception {
        Deck deck1 = new Deck(this.testDeck);
        deck1.finalDeckLog();

        Exception exception = assertThrows(IOException.class, () -> deck1.addCard(new Card(1)));
        String expectedMessage = "Cannot add to closed deck";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }


    /**
     * Tests than an error is raised when the removeCard method is run on a closed deck
     * @throws Exception
     */
    @DisplayName("Closed Remove Test")
    @Test
    public void closedRemoveTest() throws Exception {
        Deck deck1 = new Deck(this.testDeck);
        deck1.finalDeckLog();

        Exception exception = assertThrows(IOException.class, () -> deck1.removeCard());
        String expectedMessage = "Cannot remove from closed deck";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }


    /**
     * Tests that an error is raised when attemping to add to a deck twice
     * @throws Exception
     */
    @DisplayName("Double Add Test")
    @Test
    public void doubleAddTest() throws Exception {
        Deck deck1 = new Deck(this.testDeck);
        deck1.addCard(new Card(1));
        Exception exception = assertThrows(DeckLengthException.class, () -> deck1.addCard(new Card(1)));
        String expectedMessage = "Deck contains too many cards (5 or more)";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);

        deck1.removeCard();
        deck1.finalDeckLog();
    }


    /**
     * Tests that an error is raised when attemped to remove from a deck twice
     * @throws Exception
     */
    @DisplayName("Double Remove Test")
    @Test
    public void doubleRemoveTest() throws Exception {
        Deck deck1 = new Deck(this.testDeck);
        deck1.removeCard();
        Exception exception = assertThrows(DeckLengthException.class, () -> deck1.removeCard());
        String expectedMessage = "Deck contains too little cards (3 or less)";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);

        deck1.addCard(new Card(1));
        deck1.finalDeckLog();
    }
}
