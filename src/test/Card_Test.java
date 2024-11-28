package test;

import main.Card;
import main.InvalidCardException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;


class Card_Test {

    Card card;

    @BeforeEach
    void refresh() {
        card = null;
    }


    @DisplayName("Valid Values")
    @ParameterizedTest  
    @ValueSource(ints = {1, 2, 3, 4, Integer.MAX_VALUE})
    void validValuesTest(int testCase) {

        assertDoesNotThrow(() -> new Card(testCase));
    }


    @DisplayName("Invalid Values")
    @ParameterizedTest  
    @ValueSource(ints = {0, -1, -2, -12, Integer.MIN_VALUE})
    void invalidValuesTest(int testCase) {
        
        assertThrows(InvalidCardException.class, () -> new Card(testCase));
    }

    /*
     * This test requires a setter for setting example value e.g. card value = 1
     * Future tests will also need setters
     */
    @DisplayName("Set Preferred Weighting")
    @Test
    void setPreferredWeightingTest() throws InvalidCardException {
        Card card = new Card(1); //example card value
        card.setWeighting(true); // isPreferred is true, so weighting should be set to preferredWeighting (0)

        assertEquals(0, card.getWeighting()); //Weighting should be set to the preferred value (0) when isPreferred is true
    }

    @DisplayName("Set Non-Preferred Weighting")
    @Test
    void setNonPreferredWeightingTest() throws InvalidCardException {
        Card card = new Card(1); //example card value
        card.setWeighting(false); // isPreferred is false, so weighting should be set to nonPreferredWeighting (1)

        assertEquals(1, card.getWeighting()); //Weighting should be set to the non-preferred value (1) when isPreferred is false
    }


    @DisplayName("Non-Preferred Weighting Multiplier")
    @Test
    void incrementNonPreferredWeightingTest() throws InvalidCardException {
        int expectedValue = 2;
        Card card = new Card(10);
        card.setWeighting(false);
        card.incrementWeighting();
        int weighting = card.getWeighting();

        assertEquals(expectedValue, weighting);
    }

    @DisplayName("Preferred Weighting Multiplier")
    @Test
    void incrementPreferredWeightingTest() throws InvalidCardException {
        int expectedValue = 0;
        Card card = new Card(10);
        card.setWeighting(true);
        card.incrementWeighting();
        int weighting = card.getWeighting();

        assertEquals(expectedValue, weighting);
    }
}
