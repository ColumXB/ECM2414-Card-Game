//https://www.w3schools.com/java/java_files_read.asp
//TODO sort some comments
//TODO check that length of pack == 8n
//TODO make tests
package main;
import java.io.File;  // Import the File class
import java.io.FileNotFoundException;  // Import this class to handle errors
import java.util.Scanner; // Import the Scanner class to read text files

public class PackHandler {

    static int numPlayers = 4; //inputted as "number of players"
    static int packSize = 8*numPlayers; //the actual pack size
    String filepath = "testPack.txt"; //text file holding the values that will be used for pack
    public static int[] pack = new int[packSize]; //the pack

    /**
     * populates pack using the inputted text file
     * @param filepath
     * @return
     */
    public int[] packReader (String filepath) {

        int packElement = 0;
        try {
            File cardPackFile = new File(filepath);
            Scanner reader = new Scanner(cardPackFile);
            while (reader.hasNextLine()) {
                String line = reader.nextLine(); // Read each line as a String
                int data = validityCheck(line);

                /*
                * this part is where it needs to put into a list
                * increment to change the elements already within the array
                */
                pack[packElement] = data;
                packElement ++;
            }
            reader.close();
        } catch (FileNotFoundException e) { //throws an exception if the file path is invalid
            System.out.println("File Path Error.");
            e.printStackTrace(); // TODO: look into what this does
        }

        return pack;
    }

    /**
     * each line of the inputted pack is fed into the method,
     * the value is then converted from string to integer,
     * 
     * it is then checked that is satisfies the following:
     * the value is an integer
     * the value is above 0 (no negatives)
     * 
     * the integer is returned
     * @param line
     * @return
     */
    public int validityCheck(String line) throws Exception{
         
        try {
            int validInput = Integer.parseInt(line); // Convert the line to an integer

            if (validInput > 0) {
                return validInput;
            } else {
                System.out.println("Input must be greater than 0: " + line);
                throw new Exception();
                // TODO throw new exception using "throw new"
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid integer in file: " + line);
            throw e; //rethrows exception
        }
    }

    /**
     * Returns the array of values that makes up Pack
     * @return
     */
    public int[] getPack() {
        return pack;
    }

    //TODO Input for number of players (Constructor)

    public static void main(String[] args) {}
}


// test git commits