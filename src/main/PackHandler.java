//https://www.w3schools.com/java/java_files_read.asp
//https://stackoverflow.com/questions/1647907/junit-how-to-simulate-system-in-testing
//TODO make tests
package main;
import java.io.File;
import java.util.Scanner;
import java.io.FileNotFoundException;
import java.util.InputMismatchException;

public class PackHandler {

    private int numPlayers;
    private int packSize; 
    String filepath; //text file holding the values that will be used for pack
    private int[] pack; //TODO look at why this needs to be static

    /**
     * asks the user for the "number of players" and "file for pack".
     * both of these will be used in the creation of the game
     */
    public void inputs() {
        Scanner scanner = new Scanner(System.in);
            System.out.print("Please enter number of players: ");

            while (true) {
                try{
                    this.numPlayers = scanner.nextInt();
                    this.packSize = 8*numPlayers;
                    this.pack = new int[packSize];
                    //System.out.print(this.numPlayers);
                    //TODO potentially remove
                    break;
                } catch (InputMismatchException e) {
                    System.out.println("Invalid input. Please enter a valid integer:");
                    scanner.next();
                }
            }
            
            scanner.nextLine();

            //TODO check if it should be case sensitive (currently isnt)
            while (true) {
            System.out.print("Please enter location of pack to load: ");
            this.filepath = scanner.nextLine().trim();
                if (!this.filepath.isEmpty()) {
                    try {
                        validFileLength();
                        break;
                    } catch (FileLengthException e) {
                        System.out.println("Invalid input. Please enter a valid file");
                    }
                } else {
                    System.out.println("Invalid input. Please enter a non-empty string.");
                }
            }
            scanner.close();
        }

    /**
     * checks if the number of lines of the file are equal to the number needed for a valid pack
     * (based off number of players)
     * @param filepath
     * @return true if valid, FileLengthException if false
     * @throws FileLengthException
     */
    public void validFileLength() throws FileLengthException{
        int lineCount = 0;
        
        try {
            File cardPackFile = new File(this.filepath);
            Scanner reader = new Scanner(cardPackFile);
    
            while (reader.hasNextLine()) {
                reader.nextLine();
                lineCount++;
            }
    
            reader.close();
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + this.filepath);
            e.printStackTrace();
        }

        System.out.println("Required length: " + lineCount);
        System.out.println("Actual length: " + this.packSize);

        if (lineCount != this.packSize) {
            throw new FileLengthException("Error: Length of file does not match pack size.");
        }
    }

    /**
     * populates pack using the inputted text file
     * @param filepath
     * @return pack once populated
     */
    public int[] packReader () {

        int packElement = 0;
        try {
            File cardPackFile = new File(this.filepath);
            Scanner reader = new Scanner(cardPackFile);


            while (reader.hasNextLine()) {
                String line = reader.nextLine();

                try {
                    int data = validityCheck(line);
                    pack[packElement] = data;
                    packElement ++;
                } catch (InvalidCardValueException e) {
                    System.out.println("Error: " + e.getMessage());
                    e.printStackTrace();  // Optionally print stack trace for debugging
                }
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
    private int validityCheck(String line) throws InvalidCardValueException{
         
        try {
            int validInput = Integer.parseInt(line); // Convert the line to an integer

            if (validInput > 0) {
                return validInput;
            } else {
                throw new InvalidCardValueException("Input must be greater than 0: " + line);
            }
        } catch (NumberFormatException e) {
            throw new InvalidCardValueException("Invalid integer in file: " + line);
        }
    }

    /**
     * Returns the array of values that makes up Pack
     * @return
     */
    public int[] getPack() {
        return pack;
    }

    public static void main(String[] args) throws FileLengthException {

        PackHandler oops = new PackHandler();
        oops.inputs();
        oops.packReader();
        for (int singularOop:oops.getPack()) {
            System.out.print(singularOop);
            System.out.print(",");
        }
    }
}

// test git commits
