package main;
import java.io.File;
import java.util.Scanner;
import java.io.FileNotFoundException;
import java.util.InputMismatchException;

public class PackHandler {

    private int numPlayers;
    private int packSize; 
    String filePath; //text file holding the values that will be used for pack
    private int[] pack;

    /**
     * prints prompts to terminal expecting inputs used in creating/validating the pack
     */
    public void inputs() {
        //number of players
        Scanner scanner = new Scanner(System.in); // reads inputs from terminal
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

            } catch (IllegalArgumentException | InputMismatchException e) { //exceptions thrown when an invalid input is given, such as a string.
                System.out.println("Invalid input. Please enter a valid integer:");
                scanner.next(); // Clear invalid input
            }
        }

        // file path/location
        System.out.print("Please enter valid location of pack to load: ");
            while (true) {
                try {
                    initialFilePath = scanner.nextLine().trim();

                    // Validate filepath
                    filePath = validateFilePath(initialFilePath);
                    break; // Exit loop after successful validation
                } catch (IllegalArgumentException e) { // exception thrown when the file input is invalid
                    System.out.println("Invalid input. Please enter valid location of pack to load:");
                } catch (InvalidFileException e) {// exception thrown when the file input doesnt not match a necessary file location 
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
        } else if (numPlayers > 250000000) {
            throw new IllegalArgumentException("Error: Too many players. (must be less than 250,000,000)");
        }

        // create the pack
        this.numPlayers = numPlayers;
        this.packSize = 8 * numPlayers;
        this.pack = new int[packSize];

        return pack;
    }

    /**
     * uses the inputted 'filepath' and finds the input pack by file location
     * @param filePath
     * @return filePath
     */
    public String validateFilePath(String filePath) throws InvalidFileException {
        int lineCount = 0;

        // validate file location
        if (filePath == null || filePath.trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid input. Please enter a non-empty string.");
        }

        // search for file location
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
        } catch (FileNotFoundException e) { // thrown when file location cannot be found or doesnt match anything.
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

                //iterate through each line in the input pack
                //replace the empty space in pack with a valid value
                try {
                    int data = validityCheck(line);
                    pack[packElement] = data;
                    packElement ++;
                } catch (InvalidCardValueException e) { // thrown when card value is below 1 thus invalid
                    System.out.println("Error: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            reader.close();
        } catch (FileNotFoundException e) {
            System.out.println("File Path Error.");
            e.printStackTrace();
        }

        return pack;
    }

    /**
     * takes individual inputs from the input pack and checks if they are
     * valid to be put into the pack being created. 
     * @param line a line taken from the input pack to be tested for validity
     * @return valid card value to be put into the pack being created
     */
    public int validityCheck(String line) throws InvalidCardValueException{
         
        try {
            int validInput = Integer.parseInt(line); // Convert the line to an integer

            // line integer must be non-zero positive.
            if (validInput > 0) {
                return validInput;
            } else {
                throw new InvalidCardValueException("Input must be greater than 0: " + line);
            }

        // exception if line cannot be converted into an integer e.g. a lettered string
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

    /**
     * used in testing for setting an specific example pack size
     * @param packSize
     */
    public void setPackSize(int packSize) {
        this.packSize = packSize;
    }

    /**
     * @return pack size
     */
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
