//https://www.w3schools.com/java/java_files_read.asp
//https://stackoverflow.com/questions/1647907/junit-how-to-simulate-system-in-testing
package main;
import java.io.File;
import java.util.Scanner;
import java.io.FileNotFoundException;
import java.util.InputMismatchException;

public class PackHandler {

    private int numPlayers;
    private int packSize; 
    String filePath; //text file holding the values that will be used for pack
    private int[] pack; //TODO look at why this needs to be static

    public void inputs() {
        Scanner scanner = new Scanner(System.in);
        int numPlayers;
        String initialFilePath;

        while (true) {
            System.out.print("Please enter valid number of players: ");
            try {
                numPlayers = scanner.nextInt(); // Read user input
                scanner.nextLine();

                // Validate and initialize pack if input is valid
                this.pack = validateNumPlayers(numPlayers);
                break; // Exit loop after successful validation

            } catch (IllegalArgumentException | InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid integer:");
                scanner.next(); // Clear invalid input
        }
    }
    System.out.print("Please enter valid location of pack to load: ");
        while (true) {
            try {
                initialFilePath = scanner.nextLine().trim();

                // Validate filepath
                filePath = validateFilePath(initialFilePath);
                break; // Exit loop after successful validation
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid input. Please enter valid location of pack to load:");
            } catch (InvalidFileException e) {
                // Debug: Print out the exception message
                System.out.println("Invalid file. Please enter valid location of pack to load: ");
            }
        }

        scanner.close();
    }

    /**
     * numPlayers is inputted to be used in making the empty pack based of pack size
     * @param numPlayers
     * @return pack
     */
    public int[] validateNumPlayers(int numPlayers) {
        // Check if the number of players is a valid positive integer
        if (numPlayers <= 0) {
            throw new IllegalArgumentException("Error: Number of players must be a positive integer greater than 0.");
        }

        this.numPlayers = numPlayers;
        this.packSize = 8 * numPlayers;
        this.pack = new int[packSize]; // Initialize the pack array

        return pack;
    }

    /**
     * uses the inputted 'filepath' and finds the input pack by file location
     * @param filePath
     * @return filePath
     */
    public String validateFilePath(String filePath) throws InvalidFileException {
        if (filePath == null || filePath.trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid input. Please enter a non-empty string.");
        }

        System.out.println("Trying to open file: " + new File(filePath).getAbsolutePath());

        // Call validFileLength method to validate
        int lineCount = 0;
        
        try {
            File cardPackFile = new File(filePath);
        if (!cardPackFile.exists()) {
            System.out.println("File does not exist at the given path.");
            throw new InvalidFileException("File not found: " + filePath);
        }

        Scanner reader = new Scanner(cardPackFile);

        while (reader.hasNextLine()) {
            reader.nextLine();
            lineCount++;
        }

        reader.close();
    } catch (FileNotFoundException e) {
        System.out.println("File not found: " + filePath);
        throw new InvalidFileException("File not found: " + filePath);
    }

        System.out.println("Required length: " + lineCount);
        System.out.println("Actual length: " + this.packSize);

        if (lineCount != this.packSize) {
            throw new InvalidFileException("Error: Length of file does not match pack size.");
        }
        
        return filePath;
    }

    /**
     * populates pack using the inputted text file
     * @param filePath
     * @return pack once populated
     */
    public int[] packReader () {

        int packElement = 0;
        try {
            File cardPackFile = new File(this.filePath);
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
    public int validityCheck(String line) throws InvalidCardValueException{
         
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

    /**
     * @return numPlayers
     */
    public int getNumPlayers() {
        return numPlayers;
    }

    public void setPackSize(int packSize) {
        this.packSize = packSize;
    }

    public int getPackSize() {
        return this.packSize;
    }

    public static void main(String[] args) {

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
