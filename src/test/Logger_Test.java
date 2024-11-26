package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.io.File;

import main.Logger;

import java.io.IOException;
import java.nio.channels.OverlappingFileLockException;
import java.util.Scanner;

import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeAll;

public class Logger_Test {

    private String filename = "test.txt";
    private String testMessage1 = "Testing Testing 123";
    private String testMessage2 = "Periodic Table 73";


    /**
     * Creates a file for other tests to use
     * @throws IOException
     */
    @BeforeAll
    public void createFile() throws IOException {
        File file1 = new File(this.filename);
        file1.createNewFile();
    }


    /**
     * Checks constructor runs without issue
     */
    @DisplayName("Basic Constructor Check")
    @Test
    public void constructorTest() {
        assertDoesNotThrow(() -> new Logger("temp.txt").close());
    }


    /**
     * Checks that locks prevent other loggers from accessing the same file
     * @throws IOException
     */
    @DisplayName("Basic Locking Check")
    @Test
    public void lockingTest() throws IOException{

        Logger logger1 = new Logger(this.filename);
        assertThrows(OverlappingFileLockException.class, () -> new Logger(this.filename).close());
        logger1.close();
    }

    
    /**
     * Checks that the constructor clears the file of all data
     * @throws IOException
     */
    @DisplayName("Clearing Function Check")
    @Test
    public void clearTest() throws IOException {

        // Adding text
        Logger logger1 = new Logger(this.filename);
        logger1.log(this.testMessage1);
        logger1.close();

        // Check there is text before clearing
        File file1 = new File(filename);
        Scanner reader1 = new Scanner(file1);
        assertDoesNotThrow(() -> reader1.nextLine());
        reader1.close();

        // Clearing text
        Logger logger2 = new Logger(this.filename);
        logger2.close();

        // Check there is no text after clearing
        File file2 = new File(this.filename);
        Scanner reader2 = new Scanner(file2);
        assertThrows(Exception.class, () -> reader2.nextLine());
        
        reader2.close();
    }


    /**
     * Checks that text written to a file is actually written
     * @throws IOException
     */
    @DisplayName("Message Writing Check")
    @Test
    public void writeCheck() throws IOException {

        // Adding text
        Logger logger1 = new Logger(this.filename);
        logger1.log(this.testMessage1);
        logger1.close();

        // Check if text is what was written
        File file1 = new File(this.filename);
        Scanner reader1 = new Scanner(file1);
        String actualMessage = reader1.nextLine();
        assertEquals(this.testMessage1, actualMessage);
        reader1.close();
    }

    
    /**
     * Checks that a new line character is added at the end of a log
     * @throws IOException
     */
    @DisplayName("New Line Writing Check")
    @Test
    public void newLineTest() throws IOException {
        
        // Writes two new lines which will allow Scanner to successfully read one line
        Logger logger1 = new Logger(this.filename);
        logger1.log("\n");
        logger1.close();

        File file1 = new File(this.filename);
        Scanner reader1 = new Scanner(file1);
        assertDoesNotThrow(() -> reader1.nextLine());
        reader1.close();

    }


    /**
     * Checks that seperate logs on the same logger do NOT overwrite each other
     * @throws IOException
     */
    @DisplayName("Overwriting Check")
    @Test
    public void overwriteTest() throws IOException{

        String actualMessage;
        
        Logger logger1 = new Logger(this.filename);
        logger1.log(this.testMessage1);
        logger1.log(this.testMessage2);
        logger1.close();
        

        File file1 = new File(this.filename);
        Scanner reader1 = new Scanner(file1);
        actualMessage = reader1.nextLine();
        assertEquals(this.testMessage1, actualMessage);
        actualMessage = reader1.nextLine();
        assertEquals(this.testMessage2, actualMessage);
        reader1.close();
    }


    /**
     * Checks closing the logger causes no error
     * @throws IOException
     */
    @DisplayName("Basic Closure Check")
    @Test
    public void closureTest() throws IOException {
        
        Logger logger1 = new Logger(this.filename);
        assertDoesNotThrow(() -> logger1.close());
    }

    
    /**
     * Checks file is unlocked after closing a logger
     * @throws IOException
     */
    @DisplayName("Lock Release Check")
    @Test
    public void lockReleaseTest() throws IOException {

        Logger logger1 = new Logger(this.filename);
        logger1.close();

        assertDoesNotThrow(() -> new Logger(this.filename).close());
    }


    /**
     * Checks the correct error message is thrown when attemped to log with a closed logger
     * @throws IOException
     */
    @DisplayName("Closed Write Check")
    @Test
    public void closedLoggerWriteTest() throws IOException {

        // Check if logging is functional
        Logger logger1 = new Logger(this.filename);
        assertDoesNotThrow(() -> logger1.log(this.testMessage1));
        logger1.close();
        
        // Check that the correct error is raised
        Exception exception = assertThrows(IOException.class, () -> logger1.log(this.testMessage2));
        String expectedMessage = "Cannot write to closed logger";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);

        // Check no text was actually logged
        File file1 = new File(this.filename);
        Scanner reader1 = new Scanner(file1);
        reader1.nextLine();
        assertThrows(Exception.class, () -> reader1.nextLine());
        reader1.close();
    }


    /**
     * Checks that when no file is present, 
     * the logger is able to create a file without error
     */
    @DisplayName("No File Check")
    @Test
    public void noFileTest() {

        // Makes sure there is no test file present
        File file1 = new File(this.filename);
        if (file1.exists()) {
            file1.delete();
        }
        
        assertDoesNotThrow(() -> new Logger(this.filename).close());

        File file2 = new File(this.filename);
        assert file2.exists();

    }


    /**
     * Checks that closing an already closed logger will raise the correct error
     * @throws IOException
     */
    @DisplayName("Double Close Check")
    @Test
    public void doubleCloseTest() throws IOException {
        Logger logger1 = new Logger(this.filename);
        logger1.close();
        Exception exception = assertThrows(IOException.class, () -> logger1.close());
        String expectedMessage = "Cannot close closed logger";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }


    @DisplayName("Null Filename Check")
    @Test
    public void nullFilenameTest() {
        assertThrows(NullPointerException.class, () -> new Logger(null));
    }


    @DisplayName("Null Message Check")
    @Test
    public void nullMessageTest() throws IOException{
        Logger logger1 = new Logger(this.filename);
        assertThrows(NullPointerException.class, () -> logger1.log(null));
        logger1.close();
    }

}