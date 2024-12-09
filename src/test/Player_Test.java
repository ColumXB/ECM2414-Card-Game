package test;

import static org.junit.Assert.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;

import main.Card;
import main.Deck;
import main.DeckLengthException;
import main.HandLengthException;
import main.InvalidCardException;
import main.Player;

public class Player_Test {
    private Player player;
    private AtomicInteger winningPlayerId;
    private Deck pickUpDeck;
    private Deck discardDeck;

    @BeforeEach
    public void setUp() throws DeckLengthException, InvalidCardException, IOException {
        // Initial hand and decks
        int[] initialPickUpDeck = {5, 6, 7, 8};
        int[] initialDiscardDeck = {9, 10, 11, 12};
        // test dependencies
        pickUpDeck = new Deck(initialPickUpDeck);
        discardDeck = new Deck(initialDiscardDeck);
        winningPlayerId = new AtomicInteger(0);
    }

    //Tests that no exception is thrown from the constructor when the initial hand is valid
    @DisplayName("Valid Initial Hand Length")
    @Test
    public void testConstructorWithValidHand() {
        int[] validHand = {1, 2, 3, 4};  // Invalid length

        assertDoesNotThrow(() -> {
            new Player(validHand, new Boolean[4], winningPlayerId, pickUpDeck, discardDeck);
        });
    }

    //Exception is thrown from the constructor when the initial hand is invalid
    @DisplayName("Invalid Initial Hand Length")
    @Test
    public void testConstructorWithInvalidHand() {
        int[] invalidHand = {1, 2};  // Invalid length

        assertThrows(NullPointerException.class, () -> {
            new Player(invalidHand, new Boolean[4], winningPlayerId, pickUpDeck, discardDeck);
        });
    }

    //Exception is thrown from the constructor when the initial hand is null
    @DisplayName("Null Initial Hand")
    @Test
    public void testConstructorWithNullHand() {
        assertThrows(NullPointerException.class, () -> {
            new Player(null, new Boolean[4], winningPlayerId, pickUpDeck, discardDeck);
        });
    }

    //Tests that true is returned from winCheck() when a winning hand is found
    @DisplayName("Winning Hand Win Check")
    @Test
    public void testWinCheckWithWinningHand() throws DeckLengthException, InvalidCardException, IOException, HandLengthException, InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        // Setting up a hand with all same values (winning hand)
        int[] winningHand = {1, 1, 1, 1};  
        player = new Player(winningHand, new Boolean[4], winningPlayerId, pickUpDeck, discardDeck);

        Method winCheckMethod = Player.class.getDeclaredMethod("winCheck");
        winCheckMethod.setAccessible(true);

        boolean result = (boolean) winCheckMethod.invoke(player);

        assertTrue(result, "The hand should be a winning hand with all cards matching.");
    }

    //Tests that false is returned from winCheck() when a winning hand is not found
    @DisplayName("Non-Winning Hand Win Check")
    @Test
    public void testWinCheckWithNonWinningHand() throws Exception {
        int[] nonWinningHand = {1, 2, 1, 1};  
        player = new Player(nonWinningHand, new Boolean[4], winningPlayerId, pickUpDeck, discardDeck);

        Method winCheckMethod = Player.class.getDeclaredMethod("winCheck");
        winCheckMethod.setAccessible(true);

        boolean result = (boolean) winCheckMethod.invoke(player);

        assertFalse(result, "The hand should not be a winning hand as the cards do not match.");
    }

    //No exception thrown when there is a null in hand
    @DisplayName("Valid IsOneNull()")
    @Test
    public void testIsOneNull() throws Exception {
        int[] initialHand = {1, 2, 3, 4};
        player = new Player(initialHand, new Boolean[4], winningPlayerId, pickUpDeck, discardDeck);

        Field handField = Player.class.getDeclaredField("hand");
        handField.setAccessible(true);

        // Set up an valid hand (exactly one null)
        Card[] invalidHand = new Card[] {new Card(1), new Card(2), new Card(3), new Card(4), null};
        handField.set(player, invalidHand);

        assertDoesNotThrow(() -> {
            try {
                Method indexOfMethod = Player.class.getDeclaredMethod("isOneNull");
                indexOfMethod.setAccessible(true);
                indexOfMethod.invoke(player);
            } catch (InvocationTargetException e) {
                e.getCause().printStackTrace();
            }
        });
    }

    //Exception thrown when there are no nulls in hand
    @DisplayName("Invalid IsOneNull()")
    @Test
    public void testInvalidIsOneNull() throws Exception {
        int[] initialHand = {1, 2, 3, 4};
        player = new Player(initialHand, new Boolean[4], winningPlayerId, pickUpDeck, discardDeck);
        
        Field handField = Player.class.getDeclaredField("hand");
        handField.setAccessible(true);

        // Set up an invalid hand (not exactly one null)
        Card[] invalidHand = new Card[] {new Card(1), new Card(2), new Card(3), new Card(4), new Card(5)};
        handField.set(player, invalidHand);

        Method indexOfMethod = Player.class.getDeclaredMethod("isOneNull");
        indexOfMethod.setAccessible(true);

        assertThrows(InvocationTargetException.class, () -> {
            indexOfMethod.invoke(player);
        });
    }

    //No exception thrown when pickUp() is executed successfully
    @DisplayName("Valid Pickup")
    @Test
    public void testPickUp() throws Exception {
        int[] initialHand = {1, 2, 3, 4};
        player = new Player(initialHand, new Boolean[4], winningPlayerId, pickUpDeck, discardDeck);

        assertDoesNotThrow(() -> {
            try {
                Method indexOfMethod = Player.class.getDeclaredMethod("pickUp");
                indexOfMethod.setAccessible(true);
                indexOfMethod.invoke(player);
            } catch (InvocationTargetException e) {
                e.getCause().printStackTrace();
            }
        });
    }

    //Exception thrown when pickUp() fails
    @DisplayName("Invalid pickup")
    @Test
    public void badTestPickUp() throws Exception {
        int[] hand = {1, 2, 3, 4};  
        player = new Player(hand, new Boolean[4], winningPlayerId, pickUpDeck, discardDeck);

        Field handField = Player.class.getDeclaredField("hand");
        handField.setAccessible(true);

        Card[] invalidHand = new Card[] {new Card(1), new Card(2), new Card(3), new Card(4), new Card(5)};
        handField.set(player, invalidHand);

        Method indexOfMethod = Player.class.getDeclaredMethod("pickUp");
        indexOfMethod.setAccessible(true);

        assertThrows(InvocationTargetException.class, () -> {
            indexOfMethod.invoke(player);
        });
    }

    //No exception thrown when Discard() is executed successfully
    @DisplayName("Valid Discard()")
    @Test
    public void testDiscard() throws Exception {
        int[] hand = {1, 2, 3, 4};  
        player = new Player(hand, new Boolean[4], winningPlayerId, pickUpDeck, discardDeck);

        assertDoesNotThrow(() -> {
            try {
                Method indexOfMethod = Player.class.getDeclaredMethod("discard");
                indexOfMethod.setAccessible(true);
                indexOfMethod.invoke(player);
            } catch (InvocationTargetException e) {
                e.getCause().printStackTrace();
            }
        });
    }

    //Exception thrown when Discard() fails
    @DisplayName("Invalid Discard()")
    @Test
    public void badTestDiscard() throws Exception {
        int[] hand = {1, 2, 3, 4};  
        player = new Player(hand, new Boolean[4], winningPlayerId, pickUpDeck, discardDeck);

        Field handField = Player.class.getDeclaredField("hand");
        handField.setAccessible(true);

        Card[] invalidHand = new Card[] {new Card(1), new Card(2), new Card(3), new Card(4), new Card(5)};
        handField.set(player, invalidHand);

        Method indexOfMethod = Player.class.getDeclaredMethod("discard");
        indexOfMethod.setAccessible(true);

        assertThrows(InvocationTargetException.class, () -> {
            indexOfMethod.invoke(player);
        });
    }

    //Tests returned string is identical to expected output
    @DisplayName("HandToString()")
    @Test
    public void testHandToString() throws Exception {
        int[] hand = {1, 2, 3, 4};
        player = new Player(hand, new Boolean[4], winningPlayerId, pickUpDeck, discardDeck);
    
        Field handField = Player.class.getDeclaredField("hand");
        handField.setAccessible(true);
    
        Card[] validHand = new Card[] {new Card(1), new Card(2), new Card(3), new Card(4), null};
        handField.set(player, validHand);
    
        Method indexOfMethod = Player.class.getDeclaredMethod("handToString");
        indexOfMethod.setAccessible(true);  // Make the private method accessible
    
        String result = (String) indexOfMethod.invoke(player);
    
        assertEquals("1 2 3 4", result.trim());
    }

    //Tests resultant hand is identical to expected output
    @DisplayName("getNonNullCardValues()")
    @Test
    public void testGetNonNullCardValues() throws Exception {
        int[] hand = {1, 2, 3, 4};
        player = new Player(hand, new Boolean[4], winningPlayerId, pickUpDeck, discardDeck);
    
        Field handField = Player.class.getDeclaredField("hand");
        handField.setAccessible(true);
    
        Card[] validHand = new Card[] {new Card(1), new Card(2), new Card(3), new Card(4), null};
        handField.set(player, validHand);

        Method indexOfMethod = Player.class.getDeclaredMethod("getNonNullCardValues");
        indexOfMethod.setAccessible(true);

        int[] result = (int[]) indexOfMethod.invoke(player);

        int[] expected = {1, 2, 3, 4};

        assertArrayEquals(expected, result);
    }

    //Tests no exception thrown as discard index is never the preferred card
    @DisplayName("getDiscardCardIndex()")
    @Test
    public void testGetDiscardCardIndex() throws Exception {
        int[] hand = {1, 2, 3, 4};
        player = new Player(hand, new Boolean[4], winningPlayerId, pickUpDeck, discardDeck);
    
        Field handField = Player.class.getDeclaredField("hand");
        handField.setAccessible(true);
    
        Card card1 = new Card(1);
        Card card2 = new Card(2);
        Card card3 = new Card(3);
        Card card4 = new Card(4);
        Card card5 = new Card(5);
        card1.setWeighting(true);
        card2.setWeighting(false);
        card3.setWeighting(false);
        card4.setWeighting(false);
        card5.setWeighting(false);
        Card[] validHand = {card1, card2, card3, card4, card5};
        handField.set(player, validHand);

        Method indexOfMethod = Player.class.getDeclaredMethod("getDiscardCardIndex");
        indexOfMethod.setAccessible(true);

        int result = (int) indexOfMethod.invoke(player);

        assertNotEquals(0, result);
    }

    //Exception thrown when every card is preferred (this is an error as wincheck should have ended the game before this)
    @DisplayName("Invalid getDiscardCardIndex()")
    @Test
    public void testInvalidGetDiscardCardIndex() throws Exception {
        int[] hand = {1, 2, 3, 4};
        player = new Player(hand, new Boolean[4], winningPlayerId, pickUpDeck, discardDeck);
    
        Field handField = Player.class.getDeclaredField("hand");
        handField.setAccessible(true);
    
        Card card1 = new Card(1);
        Card card2 = new Card(2);
        Card card3 = new Card(3);
        Card card4 = new Card(4);
        Card card5 = new Card(5);
        card1.setWeighting(true);
        card2.setWeighting(true);
        card3.setWeighting(true);
        card4.setWeighting(true);
        card5.setWeighting(true);
        Card[] validHand = {card1, card2, card3, card4, card5};
        handField.set(player, validHand);

        Method indexOfMethod = Player.class.getDeclaredMethod("getDiscardCardIndex");
        indexOfMethod.setAccessible(true);

        assertThrows(InvocationTargetException.class, () -> {
            indexOfMethod.invoke(player);
        });
    }
}
