package main;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.charset.StandardCharsets;


public class OutputManager {

    private RandomAccessFile fileStream;
    private FileChannel channel;
    private FileLock lock;
    private ByteBuffer buffer;
    private boolean isClosed = false;


    /**
     * Constructor for logger, File is opened with the ability to write to it.
     * File chosen is completely cleared
     * @param filename Name of file to be written to
     * @param appendable Dictates whether the file will be appended to (true) or will clear the file and start a new (false)
     * @throws Exception There are several causes for this exception
     */
    public OutputManager(String filename, boolean appendable) throws IOException {

        if (filename == null) {
            throw new NullPointerException();
        }

        File file = new File(filename);
        file.createNewFile();

        // Opens and locks connection to file 
        this.fileStream = new RandomAccessFile(filename, "rw");
        this.channel = this.fileStream.getChannel();
        this.lock = channel.lock();

        // Deletes all contents of file
        if (appendable) {
            channel.position(fileStream.length());
        } else {
            channel.truncate(0);
        }
    }



    /**
     * Logs a message to the object's file
     * Each message is placed on a new line
     * @param message Message to be logged in the file
     * @throws IOException
     */
    public void output(String message) throws IOException {
        try {
            if (message == null) {
                throw new NullPointerException();
            }

            if (isClosed) {
                throw new IOException("Cannot write to closed OutputManager");
            }

            buffer = ByteBuffer.wrap(message.toString().getBytes(StandardCharsets.UTF_8));

            //TODO insert actual logging here
            this.channel.write(buffer);

            System.out.println(message.toString());

            // Adds a newline character (Encoding for UTF-8)
            byte[] newline = {(byte) 0x0A};
            this.channel.write(ByteBuffer.wrap(newline));
        } catch (Exception e) {
            System.out.println(e.toString());
            throw e;
        }
    }


    /**
     * Unlocks and closes connection with file
     * @throws IOException
     */
    public void close() throws IOException {
        
        if (isClosed) {
            throw new IOException("Cannot close closed OutputManager");
        }

        this.lock.release();
        this.channel.close();
        this.fileStream.close();
        this.isClosed = true;
    }
}
