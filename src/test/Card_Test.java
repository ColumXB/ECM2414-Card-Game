package test;

import main.Card;
import main.InvalidCardException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


class Card_Test {
    
    int[] passingTestCases = {1, 2, 3, 4, 999999999};
    int[] failingTestCases = {0, -12, -999999999};
    Card card;


    // TO DO: FIX
    @Test  
    @DisplayName("Test")
    void testTest() {
        for (int testCase: failingTestCases) {
            Throwable exception = assertThrows(InvalidCardException.class, () -> new Card(testCase));
            assertEquals(String.format("Card value must be a positive integer. Card Value: ", testCase), exception.getMessage());
        }
        for (int testCase: passingTestCases) {
            assertThrows(null, () -> new Card(testCase));
        }
    }

}

