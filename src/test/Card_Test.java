package test;

import main.Card;
import main.InvalidCardException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;


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
}

