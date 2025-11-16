import java.io.*;
import java.util.*;

public class GuestReservationActions {

    private static final String RESERVE_FILE = "RESERVE.txt";

    // 2. VIEW RESERVATIONS
    public static void viewReservations(Guest user) {
        System.out.println("\n=== Your Reservations ===");

        File file = new File(RESERVE_FILE);

        if (!file.exists()) {
            System.out.println("No reservations found.");
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            boolean found = false;
            boolean printBlock = false;

            while ((line = br.readLine()) != null) {

                // Start printing when we find the user's ID
                if (line.contains("Client ID: " + user.getID())) {
                    found = true;
                    printBlock = true;
                    System.out.println("\n-------------------- RESERVATION --------------------");
                    // We don't print this line, we print the one *after* it
                    // But we print the "Transaction ID" line which is *before* it
                    // This logic is tricky. Let's adjust.
                }

                // Let's try a different logic:
                // Find the start of a block, then check if it's the user's
                if (line.startsWith("Transaction ID:")) {
                    ArrayList<String> block = new ArrayList<>();
                    block.add(line); // Add the Transaction ID line
                    
                    boolean isUserBlock = false;
                    while ((line = br.readLine()) != null && !line.startsWith("========================================================")) {
                        block.add(line);
                        if (line.contains("Client ID: " + user.getID())) {
                            isUserBlock = true;
                        }
                    }
                    
                    // Now, if it was the user's block, print it
                    if (isUserBlock) {
                        found = true;
                        System.out.println("\n--------------------------------------------------------");
                        for (String blockLine : block) {
                            System.out.println(blockLine);
                        }
                    }
                    // Add the closing line
                    System.out.println("========================================================");
                }
            } // end while

            if (!found) {
                System.out.println("You have no reservations yet.");
            }

        } catch (IOException e) {
            System.out.println("Error reading reservations: " + e.getMessage());
        }
    }


    // --- CHANGE 1: Removed the redundant 'requestCancellation' method ---
    // This method was inferior to the one in GuestCancelReservations.
    // Keeping it here is confusing. This class is now only for VIEWING.
    /*
    public static void requestCancellation(Guest user) {
        // ... (code removed) ...
    }
    */
}