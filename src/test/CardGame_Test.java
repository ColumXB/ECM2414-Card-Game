package test;

import main.CardGame;
import main.CardGame.Utility;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

public class CardGame_Test {
    CardGame game = new CardGame();

    @DisplayName("No Nulls WinCheck")
    @Test
    public void testArrayFullWithNoNullValues() {
        Boolean[] winCheckArray = {true, false, true, false};
        assertTrue(Utility.isWinCheckArrayFull(winCheckArray));
    }

    @DisplayName("One Null WinCheck")
    @Test
    public void testArrayContainsNullValue() {
        Boolean[] winCheckArray = {true, null, false, true};
        assertFalse(Utility.isWinCheckArrayFull(winCheckArray));
    }

    @DisplayName("Only Nulls WinCheck")
    @Test
    public void testArrayOnlyContainsNullValues() {
        Boolean[] winCheckArray = {null, null, null};
        assertFalse(Utility.isWinCheckArrayFull(winCheckArray));
    }

    @DisplayName("Empty WinCheck")
    @Test
    public void testEmptyArray() {
        Boolean[] winCheckArray = {};
        assertTrue(Utility.isWinCheckArrayFull(winCheckArray));  // Empty array is considered full
    }

    @DisplayName("One Null Only WinCheck")
    @Test
    public void testArrayWithOneElementNull() {
        Boolean[] winCheckArray = {null};
        assertFalse(Utility.isWinCheckArrayFull(winCheckArray));
    }

    @DisplayName("One Element Only WinCheck")
    @Test
    public void testArrayWithOneElementNonNull() {
        Boolean[] winCheckArray = {true};
        assertTrue(Utility.isWinCheckArrayFull(winCheckArray));
    }

    @DisplayName("One true value at the start WinningIndex")
    @Test
    public void testArrayWithTrueAtStart() {
        Boolean[] winCheckArray = {true, false, false, false};
        assertEquals(0, Utility.findWinningIndex(winCheckArray)); // First index should be 0
    }

    @DisplayName("One true value at the end WinningIndex")
    @Test
    public void testArrayWithTrueAtEnd() {
        Boolean[] winCheckArray = {false, false, false, true};
        assertEquals(3, Utility.findWinningIndex(winCheckArray));
    }

    @DisplayName("Multiple true values WinningIndex")
    @Test
    public void testArrayWithMultipleTrues() {
        Boolean[] winCheckArray = {false, true, false, true};
        assertEquals(1, Utility.findWinningIndex(winCheckArray)); // First true is at index 1
    }

    @DisplayName("No true values WinningIndex")
    @Test
    public void testArrayWithNoTrues() {
        Boolean[] winCheckArray = {false, false, false, false};
        assertEquals(-1, Utility.findWinningIndex(winCheckArray)); // No true values, should return -1
    }

    @DisplayName("All null values (should throw NullPointerException) WinningIndex")
    @Test
    public void testArrayWithAllNullValues() {
        Boolean[] winCheckArray = {null, null, null, null};
        assertThrows(NullPointerException.class, () -> {
            Utility.findWinningIndex(winCheckArray); // Should throw NullPointerException
        });
    }
}
