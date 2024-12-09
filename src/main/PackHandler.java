package main;
import java.io.File;
import java.util.Scanner;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.InputMismatchException;

public class PackHandler {

    private int numPlayers;
    private int packSize; 
    String filePath; //text file holding the values that will be used for pack
    private int[] pack;
    private int[][] playerPacks;
    private int[][] deckPacks;
    private int[][] playerPacksArray;
    private int[][] deckPacksArray;
    private int[][][] gameArray;

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
                validateNumPlayers(numPlayers);
                break; // Exit loop after successful validation

            } catch (IllegalArgumentException | InputMismatchException e) {
                ThreadedLogger.log("Invalid integer input for numPlayers");
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

                    //Catch but don't throw exceptions to allow for the code to re-run until valid input
                } catch (IllegalArgumentException e) {
                    ThreadedLogger.log("IllegalArgumentException for valid pack location.");
                    System.out.println("Invalid input. Please enter valid location of pack to load:");
                } catch (InvalidFileException e) {
                    ThreadedLogger.log("InvalidFileException for valid pack location."); 
                    System.out.println("Invalid file. Please enter valid location of pack to load: ");
                } catch (FileNotFoundException e) {
                    ThreadedLogger.log("FileNotFoundException for valid pack location."); 
                    System.out.println("File not found. Please enter valid location of pack to load: ");
                }
            }

        scanner.close();
    }

    /**
     * numPlayers is inputted to be used in making the empty pack based of pack size
     * @param numPlayers
     * @return pack
     */
    private void validateNumPlayers(int numPlayers) {
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
    }

    /**
     * uses the inputted 'filepath' and finds the input pack by file location
     * @param filePath
     * @return filePath
     */
    private String validateFilePath(String filePath) throws InvalidFileException, FileNotFoundException, IllegalArgumentException {
        int lineCount = 0;

        // validate file location
        if (filePath == null || filePath.trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid input. Please enter a non-empty string.");
        }

        // search for file location
        File cardPackFile = new File(filePath);

        try{
            Scanner reader = new Scanner(cardPackFile);
            while (reader.hasNextLine()) {
                reader.nextLine();
                lineCount++;
            }
            reader.close();
        } catch (FileNotFoundException e) {
            ThreadedLogger.log("FileNotFoundException, File not found: " + filePath); 
            throw e;
        }

        if (lineCount != this.packSize) {
            ThreadedLogger.log("Error: Length of file does not match pack size.");
            throw new InvalidFileException("Error: Length of file does not match pack size.");
        }
        
        return filePath;
    }

    /**
     * populates pack using the inputted text file
     * @param filePath
     * @return pack once populated
     */
    public void packReader() throws InvalidCardException, FileNotFoundException {

        int packElement = 0;
        Scanner reader;
        try {
            File cardPackFile = new File(this.filePath);
            reader = new Scanner(cardPackFile);

            while (reader.hasNextLine()) {
                String line = reader.nextLine();

                //iterate through each line in the input pack replacing empty space in pack with a valid value
                try {
                    int data = validityCheck(line);
                    pack[packElement] = data;
                    packElement ++;
                } catch (InvalidCardException e) {
                    int errorLine = packElement+1;
                    ThreadedLogger.log("InvalidCardException in packReader(), Invalid card in input pack in line " + errorLine);
                    reader.close();
                    throw e;
                }
            }
            reader.close();
            
        } catch (FileNotFoundException e) {
            ThreadedLogger.log("FileNotFoundException in packReader()");
            throw e;
        }
    }

    /**
     * takes individual inputs from the input pack and checks if they are
     * valid to be put into the pack being created. 
     * @param line a line taken from the input pack to be tested for validity
     * @return valid card value to be put into the pack being created
     */
    private int validityCheck(String line) throws InvalidCardException{
         
        try {
            int validInput = Integer.parseInt(line); // Convert the line to an integer

            // line integer must be non-zero positive.
            if (validInput > 0) {
                return validInput;
            } else {
                throw new InvalidCardException("Input must be greater than 0: " + line);
            }

        // exception if line cannot be converted into an integer e.g. a lettered string
        } catch (NumberFormatException e) {
            ThreadedLogger.log("InvalidCardException, Invalid integer in file: " + line);
            throw new InvalidCardException("Invalid integer in file: " + line);
        }
    }


    /**
     * @return numPlayers
     */
    public int getNumPlayers() {
        return numPlayers;
    }

    /**
     * Splits the main pack into player and deck packs, along with distribution of cards in round-robin fashion.
     * @param pack
     * @return 3D 'gameArray' of player and deck packs.
     */
    public int[][][] packSplitter() {
        if (this.pack.length != numPlayers * 8) {
            throw new IllegalArgumentException("Pack size must be 8 times the number of players.");
        }

        //initialise packs
        playerPacksArray = new int[numPlayers][4];
        deckPacksArray = new int[numPlayers][4];
        gameArray = new int[2][][];

        // Split full pack array
        int middleOfPack = pack.length / 2;
        int[] fullPlayerPack = Arrays.copyOfRange(pack, 0, middleOfPack);
        int[] fullDeckPack = Arrays.copyOfRange(pack, middleOfPack, pack.length);

        // Round-robin distribution for packs
        playerPacks = distributePack(fullPlayerPack);
        deckPacks = distributePack(fullDeckPack);

        // Group the individual packs into the respective arrays
        playerPacksArray = groupPacks(playerPacks);
        deckPacksArray = groupPacks(deckPacks);

        // Create the final gameArray as a two-part array
        gameArray[0] = playerPacksArray; // Group 1: Players
        gameArray[1] = deckPacksArray;   // Group 2: Decks

        return gameArray;
    }

    /**
     * distribute pack elements into player/deck packs
     * @param fullPack
     * @return packs
     */
    private int[][] distributePack(int[] fullPack) {
        int[][] packs = new int[numPlayers][4];
        for (int i = 0; i < fullPack.length; i++) {
            int targetPack = i % numPlayers;  // Which player pack to put the element in
            int position = i / numPlayers;    // Position in the target pack
            packs[targetPack][position] = fullPack[i];
        }
        return packs;
    }

    /**
     * group packs into array
     * @param packs
     * @return groupedPacks
     */
    private int[][] groupPacks(int[][] packs) {
        int[][] groupedPacks = new int[numPlayers][4];
        for (int i = 0; i < numPlayers; i++) {
            groupedPacks[i] = packs[i];  // Group the packs into the array
        }
        return groupedPacks;
    }

    public static void main(String[] args) {}
}
