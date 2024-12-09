package test;

import main.InvalidCardException;
import main.InvalidFileException;
import main.PackHandler;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.FileNotFoundException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class PackHandler_Test {

    PackHandler oops;

    /**
     * tests each value expecting no assertion to be thrown
     * this test uses examples valid values for inputted number of players
     * no exception is thrown as the values are valid
     * @param numPlayers
     */
    @DisplayName("Valid number of players input")
    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4})
    public void validNumPlayersTest(int numPlayers) {
        PackHandler packHandler = new PackHandler();

        assertDoesNotThrow(() -> {
            Method indexOfMethod = PackHandler.class.getDeclaredMethod("validateNumPlayers", int.class);
            indexOfMethod.setAccessible(true);
            indexOfMethod.invoke(packHandler, numPlayers);
        });   
    }

    /**
     * the other side of the previous test
     * tests example invalid values for inputted number of players
     * IllegalArgumentException is expected to be thrown as the values are invalid
     * @param numPlayers
     */
    @DisplayName("Invalid number of players input")
    @ParameterizedTest  
    @ValueSource(ints = {Integer.MAX_VALUE, 0, -1, -2, -12, Integer.MIN_VALUE})
    public void invalidNumPlayersTest(int numPlayers) throws IllegalArgumentException {
        PackHandler packHandler = new PackHandler();

        InvocationTargetException exception = assertThrows(InvocationTargetException.class, () -> {
            Method indexOfMethod = PackHandler.class.getDeclaredMethod("validateNumPlayers", int.class);
            indexOfMethod.setAccessible(true);
            indexOfMethod.invoke(packHandler, numPlayers);
        });

        assertTrue(exception.getCause() instanceof IllegalArgumentException, 
                "Expected IllegalArgumentException but got: " + exception.getCause());
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
    public void validFilePathTest(String filePath) {
        PackHandler packHandler = new PackHandler();
        
        try{
            Field packSizeField = PackHandler.class.getDeclaredField("packSize");
            packSizeField.setAccessible(true);
            packSizeField.set(packHandler, 32);
    
            assertDoesNotThrow(() -> {
                Method indexOfMethod = PackHandler.class.getDeclaredMethod("validateFilePath", String.class);
                indexOfMethod.setAccessible(true);
                indexOfMethod.invoke(packHandler, filePath);
            });
        } catch (NoSuchFieldException | IllegalAccessException e) {
            // Handle the reflection-related exceptions
            e.printStackTrace();
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    /**
     * the other side of the previous test
     * tests example invalid location for inputted file path
     * InvalidFileException is expected to be thrown as the location is invalid
     * @param filePath
     */
    @DisplayName("Invalid File Path")
    @ParameterizedTest  
    @ValueSource(strings = {"a.txt"})
    public void invalidFilePathTest(String filePath) throws FileNotFoundException, InvalidFileException {
        PackHandler packHandler = new PackHandler();

        try{
            Field packSizeField = PackHandler.class.getDeclaredField("packSize");
            packSizeField.setAccessible(true);
            packSizeField.set(packHandler, 32);
    
            assertThrows(InvocationTargetException.class, () -> {
                Method indexOfMethod = PackHandler.class.getDeclaredMethod("validateFilePath", String.class);
                indexOfMethod.setAccessible(true);
                indexOfMethod.invoke(packHandler, filePath);
            });

        } catch (NoSuchFieldException | IllegalAccessException e) {
            // Handle the reflection-related exceptions
            e.printStackTrace();
            fail("Unexpected exception: " + e.getMessage());
        }
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
    public void validValidityCheckTest(String line) {
        PackHandler packHandler = new PackHandler();

        // Assert that no exception is thrown for valid inputs
        assertDoesNotThrow(() -> {
            Method indexOfMethod = PackHandler.class.getDeclaredMethod("validityCheck", String.class);
            indexOfMethod.setAccessible(true);
            indexOfMethod.invoke(packHandler, line);
        });
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
    public void invalidValidityCheckTest(String line) throws InvalidCardException {
        PackHandler packHandler = new PackHandler();
    
        assertThrows(InvocationTargetException.class, () -> {
            Method indexOfMethod = PackHandler.class.getDeclaredMethod("validityCheck", String.class);
            indexOfMethod.setAccessible(true);
            indexOfMethod.invoke(packHandler, line);
        });
    }
}
