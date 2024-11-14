package test;

import main.InvalidCardValueException;
import main.InvalidFileException;
import main.PackHandler;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class PackHandler_Test {

    PackHandler oops;

    // ran before tests to prevent previous testing from interfering with current testing
    @BeforeAll
    static void refresh() {
        
    }

    /**
     * tests each value expecting no assertion to be thrown
     * this test uses examples valid values for inputted number of players
     * no exception is thrown as the values are valid
     * @param numPlayers
     */
    @DisplayName("Valid number of players input")
    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4})
    void validNumPlayersTest(int numPlayers) {
        PackHandler packHandler = new PackHandler();

        // Assert that no exception is thrown for valid inputs
        assertDoesNotThrow(() -> packHandler.validateNumPlayers(numPlayers));
    }

    /**
     * the other side of the previous test
     * tests example invalid values for inputted number of players
     * IllegalArgumentException is expected to be thrown as the values are invalid
     * @param numPlayers
     */
    @DisplayName("Invalid number of players input")
    @ParameterizedTest  
    @ValueSource(ints = {0, -1, -2, -12, Integer.MIN_VALUE})
    void invalidNumPlayersTest(int numPlayers) {
        PackHandler packHandler = new PackHandler();
    
        // Assert that an IllegalArgumentException is thrown for invalid inputs
        assertThrows(IllegalArgumentException.class, () -> packHandler.validateNumPlayers(numPlayers));
    }

    /**
     * tests example file location expecting no assertion to be thrown
     * this test uses examples valid file location for inputted file path
     * no exception is thrown as the location is valid
     * @param filePath
     */
    @DisplayName("Valid File Path")
    @ParameterizedTest  
    @ValueSource(strings = {"testPack.txt"})
    void validFilePathTest(String filePath) {
        PackHandler packHandler = new PackHandler();
        packHandler.setPackSize(32);
    
        // Assert that no exception is thrown for valid inputs
        assertDoesNotThrow(() -> packHandler.validateFilePath(filePath));
    }

    /**
     * the other side of the previous test
     * tests example invalid location for inputted file path
     * InvalidFileException is expected to be thrown as the location is invalid
     * @param filePath
     */
    @DisplayName("Invalid File Path")
    @ParameterizedTest  
    @ValueSource(strings = {"a.txt", "b.txt", "c.txt", "d.txt"})
    void invalidFilePathTest(String filePath) {
        PackHandler packHandler = new PackHandler();
    
        // Assert that an InvalidFileException is thrown for invalid inputs
        assertThrows(InvalidFileException.class, () -> packHandler.validateFilePath(filePath));
    }
    
    /**
     * tests each value expecting no assertion to be thrown
     * this test uses examples valid values for inputted card values
     * no exception is thrown as the values are valid
     * @param line
     */
    @DisplayName("Valid line value for card")
    @ParameterizedTest
    @ValueSource(strings = {"1", "2", "3", "4", "2147483647"})
    void validValidityCheckTest(String line) {
        PackHandler packHandler = new PackHandler();

        // Assert that no exception is thrown for valid inputs
        assertDoesNotThrow(() -> packHandler.validityCheck(line));
    }

    /**
     * the other side of the previous test
     * tests example invalid values for inputted card values
     * InvalidCardValueException is expected to be thrown as the values are invalid
     * @param line
     */
    @DisplayName("Invalid line value for card")
    @ParameterizedTest  
    @ValueSource(strings = {"a", "-1", "-2", "-3", "-2147483647"})
    void invalidValidityCheckTest(String line) {
        PackHandler packHandler = new PackHandler();
    
        // Assert that an InvalidCardValueException is thrown for invalid inputs
        assertThrows(InvalidCardValueException.class, () -> packHandler.validityCheck(line));
    }
}
