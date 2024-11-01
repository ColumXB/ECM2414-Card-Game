package main;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class Logger {

    private String entityName;
    private File logFile;
    private FileWriter logFileWriter;


    /**
     * Logs a message to the object's file
     * @param message Message to be logged in the file
     * @throws IOException
     */
    public void log(String message) throws IOException {
        logFileWriter.write(message);
    }


    /**
     * Safely closes connection with file
     * @throws IOException
     */
    public void close() throws IOException {
        logFileWriter.close();
    }


    /**
     * Constructor for logger, File is opened with the ability to write to it.
     * File chosen is completely overwritten
     * @param entityName Name of entity that the file is related to
     * @throws IOException There are several causes for this exception
     */
    public Logger(String entityName) throws IOException {

        this.entityName = entityName;
        String filename = String.format(entityName, "_output.txt");

        this.logFile = new File(filename);
        this.logFile.createNewFile(); // Throws IOException

        // 
        if (this.logFile.canWrite()) {
            this.logFileWriter = new FileWriter(filename);
        } else {
            throw new IOException("Cannot write to file");
        }
    }


    public static void main(String[] args) throws IOException {
        Logger oops = new Logger("test");
        oops.log("testing testing 123");
        oops.close();
        
    }
}
